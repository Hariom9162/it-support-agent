package com.veridian.it_support_agent.service;

import com.veridian.it_support_agent.dto.AgentRequest;
import com.veridian.it_support_agent.dto.AgentResponse;
import com.veridian.it_support_agent.entity.KnowledgeBase;
import com.veridian.it_support_agent.entity.Ticket;
import com.veridian.it_support_agent.repository.KnowledgeBaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final EscalationService escalationService;
    private final AuditLogService auditLogService;
    @Autowired
    private GeminiService geminiService;
    /*
     * Temporary conversation state.
     *
     * Key   = employee name
     * Value = previous message waiting for clarification
     */
    private final Map<String, String> pendingConversations =
            new ConcurrentHashMap<>();

    public AgentService(
            KnowledgeBaseRepository knowledgeBaseRepository,
            EscalationService escalationService,
            AuditLogService auditLogService) {

        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.escalationService = escalationService;
        this.auditLogService = auditLogService;

    }

    public AgentResponse processRequest(AgentRequest request) {

        String message = request.getMessage();
        String employeeName = request.getEmployeeName();

        /*
         * STEP 1: Validate request
         */
        if (message == null || message.trim().isEmpty()) {

            auditLogService.saveAudit(
                    "",
                    "Please describe your IT issue.",
                    "UNKNOWN",
                    null,
                    "Asked employee to provide an issue",
                    false
            );

            return new AgentResponse(
                    "Please describe your IT issue so I can help you.",
                    "FOLLOW_UP",
                    null,
                    null,
                    false
            );
        }

        String normalizedMessage = message.toLowerCase().trim();

        /*
         * STEP 2: Check whether this employee
         * already has a pending clarification.
         */
        String previousMessage =
                pendingConversations.get(employeeName);

        if (previousMessage != null) {

            /*
             * Combine previous issue + employee clarification.
             *
             * Example:
             *
             * Previous:
             * "Something is not working with my computer"
             *
             * Current:
             * "My laptop is completely dead"
             *
             * Combined message gives the policy matcher
             * more context.
             */
            String combinedMessage =
                    previousMessage + " " + normalizedMessage;

            /*
             * Security must still be checked first.
             */
            if (isSecurityRisk(normalizedMessage)) {

                pendingConversations.remove(employeeName);

                Ticket ticket = escalationService.escalate(
                        employeeName,
                        message
                );

                auditLogService.saveAudit(
                        message,
                        "",
                        "SECURITY_INCIDENT",
                        "KB-09",
                        "Escalated to human/security review",
                        true
                );

                return new AgentResponse(
                        "This request involves a potential security incident. " +
                                "I have escalated it for human/security review.",
                        "ESCALATED",
                        "KB-09",
                        ticket.getTicketId(),
                        true
                );
            }

            /*
             * Try to resolve using the clarification.
             */
            KnowledgeBase clarifiedPolicy =
                    findRelevantPolicy(addAiContext(combinedMessage));

            /*
             * If clarification is still unclear,
             * ask another question.
             */
            if (clarifiedPolicy == null) {

                auditLogService.saveAudit(
                        message,
                        "Please provide more specific details about the issue.",
                        "UNKNOWN",
                        null,
                        "Clarification was still insufficient",
                        false
                );

                return new AgentResponse(
                        "Thanks. I still need a little more information. " +
                                "Could you describe exactly what is not working " +
                                "or mention the affected service/device?",
                        "FOLLOW_UP",
                        null,
                        null,
                        false
                );
            }

            /*
             * Clarification successfully resolved the intent.
             */
            pendingConversations.remove(employeeName);

            auditLogService.saveAudit(
                    message,
                    "",
                    clarifiedPolicy.getTitle(),
                    clarifiedPolicy.getKbCode(),
                    "Resolved request after employee clarification",
                    false
            );

            return new AgentResponse(
                    clarifiedPolicy.getContent(),
                    "RESOLVED",
                    clarifiedPolicy.getKbCode(),
                    null,
                    false
            );
        }

        /*
         * STEP 3: Detect security risks
         */
        if (isSecurityRisk(normalizedMessage)) {

            Ticket ticket = escalationService.escalate(
                    employeeName,
                    message
            );

            auditLogService.saveAudit(
                    message,
                    "",
                    "SECURITY_INCIDENT",
                    "KB-09",
                    "Escalated to human/security review",
                    true
            );

            return new AgentResponse(
                    "This request involves a potential security incident. " +
                            "I have escalated it for human/security review.",
                    "ESCALATED",
                    "KB-09",
                    ticket.getTicketId(),
                    true
            );
        }

        /*
         * STEP 4: Search Knowledge Base
         */
        KnowledgeBase policy =
                findRelevantPolicy(addAiContext(normalizedMessage));

        /*
         * STEP 5: No confident match
         *
         * Save the current message so that the
         * next employee response can continue
         * this conversation.
         */
        if (policy == null) {

            pendingConversations.put(
                    employeeName,
                    normalizedMessage
            );

            auditLogService.saveAudit(
                    message,
                    "Can you provide more details about the issue?",
                    "UNKNOWN",
                    null,
                    "Asked follow-up question because the issue was ambiguous",
                    false
            );

            return new AgentResponse(
                    "I want to make sure I understand your issue correctly. " +
                            "Could you tell me whether the problem is related to " +
                            "your laptop, VPN/network, password/account, software, " +
                            "printer, Wi-Fi, or something else?",
                    "FOLLOW_UP",
                    null,
                    null,
                    false
            );
        }

        /*
         * STEP 6: Return policy-based resolution
         */
        String intent = policy.getTitle();

        auditLogService.saveAudit(
                message,
                "",
                intent,
                policy.getKbCode(),
                "Provided resolution based on knowledge base policy",
                false
        );

        return new AgentResponse(
                policy.getContent(),
                "RESOLVED",
                policy.getKbCode(),
                null,
                false
        );
    }

    /*
     * Security-related requests must be escalated.
     */
    private boolean isSecurityRisk(String message) {

        return message.contains("phishing")
                || message.contains("suspicious email")
                || message.contains("security incident")
                || message.contains("hacked")
                || message.contains("malware")
                || message.contains("ransomware");
    }

    /*
     * Finds a confident policy match.
     */
    private KnowledgeBase findRelevantPolicy(String message) {

        List<KnowledgeBase> policies =
                knowledgeBaseRepository.findAll();

        KnowledgeBase bestMatch = null;
        int highestScore = 0;

        for (KnowledgeBase policy : policies) {

            int score = calculatePolicyScore(
                    message,
                    policy.getKbCode()
            );

            if (score > highestScore) {
                highestScore = score;
                bestMatch = policy;
            }
        }

        /*
         * Normal requests require at least
         * two meaningful signals.
         */
        if (highestScore < 2) {
            return null;
        }

        return bestMatch;
    }

    /*
     * Policy-specific keyword matching.
     */
    private int calculatePolicyScore(
            String message,
            String kbCode) {

        int score = 0;

        switch (kbCode) {

            case "KB-01":

                score += containsAny(
                        message,
                        "password",
                        "forgot password",
                        "reset password",
                        "locked out",
                        "account locked"
                );

                score += containsAny(
                        message,
                        "reset",
                        "forgot",
                        "failed attempts",
                        "unlock"
                );

                break;

            case "KB-02":

                score += containsAny(
                        message,
                        "vpn",
                        "virtual private network"
                );

                score += containsAny(
                        message,
                        "credentials",
                        "expired",
                        "renew",
                        "vpn access",
                        "connect to vpn"
                );

                break;

            case "KB-03":

                score += containsAny(
                        message,
                        "laptop",
                        "notebook",
                        "computer"
                );

                score += containsAny(
                        message,
                        "dead",
                        "won't turn on",
                        "doesn't turn on",
                        "not turning on",
                        "hardware failure",
                        "replacement",
                        "broken"
                );

                break;

            case "KB-04":

                score += containsAny(
                        message,
                        "software",
                        "application",
                        "app",
                        "program"
                );

                score += containsAny(
                        message,
                        "install",
                        "installation",
                        "installing",
                        "approval"
                );

                break;

            case "KB-05":

                score += containsAny(
                        message,
                        "printer",
                        "printing",
                        "print"
                );

                score += containsAny(
                        message,
                        "paper jam",
                        "printer queue",
                        "spooler",
                        "not printing",
                        "can't print"
                );

                break;

            case "KB-06":

                score += containsAny(
                        message,
                        "mailbox",
                        "email storage",
                        "email quota",
                        "mail quota"
                );

                score += containsAny(
                        message,
                        "full",
                        "quota",
                        "storage",
                        "space"
                );

                break;

            case "KB-07":

                score += containsAny(
                        message,
                        "guest wifi",
                        "guest wi-fi",
                        "guest wireless"
                );

                score += containsAny(
                        message,
                        "guest",
                        "wifi",
                        "wi-fi",
                        "wireless"
                );

                break;

            case "KB-08":

                score += containsAny(
                        message,
                        "expense",
                        "expense software",
                        "expense tool"
                );

                score += containsAny(
                        message,
                        "access",
                        "approval",
                        "permission"
                );

                break;

            case "KB-09":

                score += containsAny(
                        message,
                        "security",
                        "phishing",
                        "malware",
                        "hacked",
                        "ransomware"
                );

                score += containsAny(
                        message,
                        "incident",
                        "suspicious",
                        "attack",
                        "compromised"
                );

                break;

            case "KB-10":

                score += containsAny(
                        message,
                        "work from home",
                        "work-from-home",
                        "wfh",
                        "home equipment"
                );

                score += containsAny(
                        message,
                        "monitor",
                        "keyboard",
                        "mouse",
                        "home office",
                        "equipment"
                );

                break;

            default:
                break;
        }

        return score;
    }

    /*
     * Returns 1 if any keyword exists.
     */
    private int containsAny(
            String message,
            String... keywords) {

        for (String keyword : keywords) {

            if (message.contains(keyword)) {
                return 1;
            }
        }

        return 0;
    }
    private String addAiContext(String message) {

        String label = geminiService.classifyIssue(message);

        return switch (label) {
            case "PASSWORD" -> message + " password reset";
            case "VPN" -> message + " VPN credentials";
            case "LAPTOP" -> message + " laptop hardware";
            case "SOFTWARE" -> message + " software installation";
            case "PRINTER" -> message + " printer paper jam";
            case "MAILBOX" -> message + " mailbox quota";
            case "GUEST_WIFI" -> message + " guest WiFi";
            case "EXPENSE" -> message + " expense access";
            case "SECURITY" -> message + " security incident";
            case "WFH_EQUIPMENT" -> message + " home office equipment";
            default -> message;
        };
    }
}