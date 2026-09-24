import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [employeeName, setEmployeeName] = useState("");
  const [message, setMessage] = useState("");
  const [response, setResponse] = useState(null);
  const [auditLogs, setAuditLogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [auditLoading, setAuditLoading] = useState(false);

  const API_BASE_URL = "http://localhost:8080";

  // Fetch audit logs from Spring Boot
  const fetchAuditLogs = async () => {
    setAuditLoading(true);

    try {
      const res = await fetch(`${API_BASE_URL}/api/audit`);

      if (!res.ok) {
        throw new Error("Failed to fetch audit logs");
      }

      const data = await res.json();

      // Latest logs first
      setAuditLogs([...data].reverse());
    } catch (error) {
      console.error("Audit fetch error:", error);
    } finally {
      setAuditLoading(false);
    }
  };

  // Load audit logs when page opens
  useEffect(() => {
    fetchAuditLogs();
  }, []);

  const askAgent = async () => {
    if (!employeeName.trim() || !message.trim()) {
      alert("Please enter your name and IT issue.");
      return;
    }

    setLoading(true);
    setResponse(null);

    try {
      const res = await fetch(`${API_BASE_URL}/api/agent/chat`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          employeeName: employeeName,
          message: message,
        }),
      });

      if (!res.ok) {
        throw new Error("Backend request failed");
      }

      const data = await res.json();

      setResponse(data);

      // Refresh audit trail after every request
      await fetchAuditLogs();

    } catch (error) {
      console.error("Agent error:", error);

      setResponse({
        answer: "Unable to connect to the IT Support Agent.",
        status: "ERROR",
        source: null,
        ticketId: null,
        escalated: false,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">

      {/* Header */}
      <header className="header">
        <div>
          <h1>IT Support Agent</h1>
          <p>Internal Service Desk</p>
        </div>

        <span className="online">
          ● Agent Online
        </span>
      </header>

      <main className="container">

        {/* Request Form */}
        <section className="card">
          <h2>How can I help you?</h2>

          <p className="subtitle">
            Describe your IT issue and our support agent will analyze it.
          </p>

          <label>Employee Name</label>

          <input
            type="text"
            placeholder="Enter your name"
            value={employeeName}
            onChange={(e) => setEmployeeName(e.target.value)}
          />

          <label>Describe your IT issue</label>

          <textarea
            rows="5"
            placeholder="Example: My laptop is completely dead"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
          />

          <button
            onClick={askAgent}
            disabled={loading}
          >
            {loading ? "Analyzing..." : "Ask Support Agent"}
          </button>
        </section>

        {/* Agent Response */}
        {response && (
          <section className="response-card">

            <div className="response-header">
              <h2>Agent Response</h2>

              <span
                className={`status ${response.status?.toLowerCase()}`}
              >
                {response.status}
              </span>
            </div>

            <div className="answer">
              {response.answer}
            </div>

            {response.source && (
              <div className="info">
                <strong>Knowledge Source:</strong>{" "}
                {response.source}
              </div>
            )}

            {response.ticketId && (
              <div className="info">
                <strong>Ticket ID:</strong>{" "}
                {response.ticketId}
              </div>
            )}

            {response.escalated && (
              <div className="escalation">
                ⚠️ This request has been escalated for human review.
              </div>
            )}

          </section>
        )}

        {/* Features */}
        <section className="features">

          <div>
            <span>🔍</span>
            <h3>Understand</h3>
            <p>Analyzes employee IT requests.</p>
          </div>

          <div>
            <span>📚</span>
            <h3>Find Policy</h3>
            <p>Uses the internal knowledge base.</p>
          </div>

          <div>
            <span>🎫</span>
            <h3>Escalate</h3>
            <p>Creates tickets for risky issues.</p>
          </div>

          <div>
            <span>📝</span>
            <h3>Audit</h3>
            <p>Maintains an audit trail.</p>
          </div>

        </section>

        {/* Audit Trail */}
        <section className="audit-section">

          <div className="audit-header">
            <div>
              <h2>Audit Trail</h2>
              <p>
                Complete record of agent decisions and actions
              </p>
            </div>

            <button
              className="refresh-button"
              onClick={fetchAuditLogs}
              disabled={auditLoading}
            >
              {auditLoading ? "Refreshing..." : "↻ Refresh"}
            </button>
          </div>

          {auditLogs.length === 0 ? (
            <div className="no-audit">
              No audit records found.
            </div>
          ) : (
            <div className="audit-list">

              {auditLogs.map((log) => (

                <div className="audit-card" key={log.id}>

                  <div className="audit-top">

                    <div>
                      <span className="audit-id">
                        #{log.id}
                      </span>

                      <span
                        className={`audit-badge ${
                          log.escalated
                            ? "escalated-badge"
                            : "normal-badge"
                        }`}
                      >
                        {log.escalated
                          ? "ESCALATED"
                          : "NORMAL"}
                      </span>
                    </div>

                    <span className="audit-time">
                      {log.createdAt
                        ? new Date(log.createdAt).toLocaleString()
                        : "-"}
                    </span>

                  </div>

                  <div className="audit-request">
                    <strong>Request</strong>
                    <p>{log.request || "-"}</p>
                  </div>

                  {log.question && (
                    <div className="audit-row">
                      <strong>Question:</strong>
                      <span>{log.question}</span>
                    </div>
                  )}

                  <div className="audit-row">
                    <strong>Intent:</strong>
                    <span>{log.intent || "-"}</span>
                  </div>

                  <div className="audit-row">
                    <strong>Source:</strong>
                    <span>{log.sourceUsed || "None"}</span>
                  </div>

                  <div className="audit-row">
                    <strong>Action:</strong>
                    <span>{log.actionTaken || "-"}</span>
                  </div>

                </div>

              ))}

            </div>
          )}

        </section>

      </main>
    </div>
  );
}

export default App;