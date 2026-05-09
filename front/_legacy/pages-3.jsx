/* global React, PageHead, Icon, StatusBadge, Stepper, useToast */
/* global fmtKRW, fmtN, fmtDate, fmtDateTime, fmtRelative */
/* global CUSTOMERS, PRODUCTS, WAREHOUSES, SUPPLIERS, QUOTES, PAYMENTS, REFUNDS */
/* global SETTLEMENT_PERIODS, LEDGERS, CRM_CUSTOMERS, CONSULTATIONS, CLAIMS */
/* global NOTIFICATIONS, REPORTS, APPROVALS, EMPLOYEES, PAYROLLS, WORK_ORDERS */
/* global lookupCustomer, lookupProduct, lookupWh */

const { useState: uS3, useMemo: uM3 } = React;

// ============================================================
// QUOTES
// ============================================================
function QuotesPage({ store, lang }) {
  const [filter, setFilter] = uS3("ALL");
  const items = uM3(() => filter === "ALL" ? QUOTES : QUOTES.filter(q => q.status === filter), [filter]);
  const counts = QUOTES.reduce((c, q) => ({ ...c, [q.status]: (c[q.status] || 0) + 1 }), { ALL: QUOTES.length });
  const STATUS = { ACTIVE: { ko: "유효", en: "Active", tone: "info" }, ACCEPTED: { ko: "수주전환", en: "Accepted", tone: "ok" }, EXPIRED: { ko: "만료", en: "Expired", tone: "neutral" }, REJECTED: { ko: "거절", en: "Rejected", tone: "danger" } };
  return (
    <>
      <PageHead title={lang === "en" ? "Quotes" : "견적 관리"}
        sub={lang === "en" ? `${QUOTES.length} quotes` : `총 ${QUOTES.length}건`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "New quote" : "견적 발행"}</button>} />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            {["ALL", "ACTIVE", "ACCEPTED", "EXPIRED", "REJECTED"].map(f => (
              <button key={f} className={`chip ${filter === f ? "on" : ""}`} onClick={() => setFilter(f)}>
                {f === "ALL" ? (lang === "en" ? "All" : "전체") : (lang === "en" ? STATUS[f].en : STATUS[f].ko)}
                <span className="count">{counts[f] || 0}</span>
              </button>
            ))}
          </div>
        </div>
        <table className="tbl">
          <thead><tr>
            <th>ID</th><th>{lang === "en" ? "Customer" : "고객"}</th><th>{lang === "en" ? "Items" : "품목"}</th>
            <th className="num">{lang === "en" ? "Total" : "총액"}</th><th>{lang === "en" ? "Valid until" : "유효기한"}</th>
            <th>{lang === "en" ? "Created" : "발행일"}</th><th>{lang === "en" ? "Status" : "상태"}</th>
          </tr></thead>
          <tbody>
            {items.map((q, i) => {
              const meta = STATUS[q.status];
              return (
                <tr key={q.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">QT-{q.id}</td>
                  <td className="strong">{lookupCustomer(q.customerId).name}</td>
                  <td className="muted">{lookupProduct(q.lines[0].productId).name}{q.lines.length > 1 ? ` 외 ${q.lines.length - 1}` : ""}</td>
                  <td className="num strong">{fmtKRW(q.total)}</td>
                  <td className="muted">{fmtDate(q.validUntil)}</td>
                  <td className="muted">{fmtDateTime(q.createdAt)}</td>
                  <td><span className={`badge ${meta.tone}`}>{lang === "en" ? meta.en : meta.ko}</span></td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// PAYMENTS
// ============================================================
function PaymentsPage({ store, lang }) {
  const [tab, setTab] = uS3("payments");
  return (
    <>
      <PageHead title={lang === "en" ? "Payments" : "결제 관리"}
        sub={lang === "en" ? `${PAYMENTS.length} payments · ${REFUNDS.length} refunds` : `결제 ${PAYMENTS.length}건 · 환불 ${REFUNDS.length}건`}
        actions={
          <div style={{ display: "flex", gap: 0, border: "1px solid var(--border)", borderRadius: 6, overflow: "hidden" }}>
            <button className={`btn btn-sm ${tab === "payments" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("payments")}>{lang === "en" ? "Payments" : "결제"}</button>
            <button className={`btn btn-sm ${tab === "refunds" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("refunds")}>{lang === "en" ? "Refunds" : "환불"}</button>
          </div>
        } />

      <div className="kpi-grid" style={{ gridTemplateColumns: "repeat(3, 1fr)" }}>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Today's Volume" : "오늘 결제액"}</div><div className="kpi-num tnum">{fmtKRW(19_996_000)}</div><div className="kpi-meta"><span className="kpi-delta up">▲ 12.4%</span><span className="vs">{lang === "en" ? "vs yesterday" : "어제 대비"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Refund Rate" : "환불률"}</div><div className="kpi-num tnum">2.9<span className="kpi-unit">%</span></div><div className="kpi-meta"><span className="kpi-delta down">▼ 0.4%</span><span className="vs">{lang === "en" ? "monthly" : "월간"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Pending" : "결제 대기"}</div><div className="kpi-num tnum">1<span className="kpi-unit">{lang === "en" ? "case" : "건"}</span></div><div className="kpi-meta"><span className="muted">{fmtKRW(2_720_000)}</span></div></div>
      </div>

      {tab === "payments" ? (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr>
              <th>ID</th><th>{lang === "en" ? "Order" : "주문"}</th><th>{lang === "en" ? "Method" : "수단"}</th>
              <th className="num">{lang === "en" ? "Amount" : "금액"}</th><th>{lang === "en" ? "Created" : "결제일시"}</th>
              <th>{lang === "en" ? "Status" : "상태"}</th><th></th>
            </tr></thead>
            <tbody>
              {PAYMENTS.map((p, i) => (
                <tr key={p.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">PAY-{p.id}</td>
                  <td className="muted">#{p.orderId}</td>
                  <td><span className="badge">{p.method}</span></td>
                  <td className="num strong">{fmtKRW(p.amount)}</td>
                  <td className="muted">{fmtDateTime(p.createdAt)}</td>
                  <td><StatusBadge status={p.status} lang={lang} /></td>
                  <td style={{ textAlign: "right" }}>{p.status === "COMPLETED" && <button className="btn btn-sm btn-ghost">{lang === "en" ? "Refund" : "환불"}</button>}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr>
              <th>ID</th><th>{lang === "en" ? "Payment" : "결제"}</th><th>{lang === "en" ? "Reason" : "사유"}</th>
              <th className="num">{lang === "en" ? "Amount" : "금액"}</th><th>{lang === "en" ? "Date" : "일시"}</th><th>{lang === "en" ? "Status" : "상태"}</th>
            </tr></thead>
            <tbody>
              {REFUNDS.map((r, i) => (
                <tr key={r.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">RFD-{r.id}</td>
                  <td className="muted">PAY-{r.paymentId}</td>
                  <td>{r.reason}</td>
                  <td className="num strong">{fmtKRW(r.amount)}</td>
                  <td className="muted">{fmtDateTime(r.createdAt)}</td>
                  <td><StatusBadge status={r.status} lang={lang} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}

// ============================================================
// SETTLEMENT
// ============================================================
function SettlementPage({ lang }) {
  const [periodId, setPeriodId] = uS3(32);
  const period = SETTLEMENT_PERIODS.find(p => p.id === periodId);
  const ledgers = LEDGERS.filter(l => l.periodId === periodId);
  const totalDr = ledgers.filter(l => l.amount > 0).reduce((s, l) => s + l.amount, 0);
  const totalCr = ledgers.filter(l => l.amount < 0).reduce((s, l) => s + Math.abs(l.amount), 0);
  const TYPE = { SALES: "ok", REFUND: "danger", PURCHASE: "warn", FEE: "neutral", ADJUSTMENT: "info", REVERSAL: "neutral" };

  return (
    <>
      <PageHead title={lang === "en" ? "Settlement" : "정산 / 회계"}
        sub={lang === "en" ? "Periods, ledgers and seller settlements" : "정산 기간 · 전표 · 판매자 정산"}
        actions={<>
          <button className="btn"><Icon.Plus /> {lang === "en" ? "New period" : "기간 개설"}</button>
          <button className="btn btn-accent">{lang === "en" ? "Close period" : "기간 마감"}</button>
        </>} />

      <div className="card" style={{ marginBottom: 14 }}>
        <div className="card-head">
          <div className="card-title">{lang === "en" ? "Periods" : "정산 기간"}</div>
        </div>
        <div className="card-body" style={{ padding: 0 }}>
          {SETTLEMENT_PERIODS.map(p => (
            <div key={p.id} onClick={() => setPeriodId(p.id)}
              style={{ padding: "12px 18px", borderTop: "1px solid var(--divider)", display: "grid", gridTemplateColumns: "100px 1fr 1fr 120px 120px", gap: 16, alignItems: "center", cursor: "pointer", background: p.id === periodId ? "var(--accent-soft)" : "transparent", transition: "background 0.15s" }}>
              <span className="mono muted">P-{p.id}</span>
              <span className="strong">{p.startDate} ~ {p.endDate}</span>
              <span className="muted">{lang === "en" ? "Sales" : "매출"} <span className="strong tnum">{fmtKRW(p.totalSales)}</span></span>
              <span className="muted tnum">{lang === "en" ? "Refund" : "환불"} {fmtKRW(p.totalRefund)}</span>
              <span><span className={`badge ${p.status === "OPEN" ? "info" : p.status === "CLOSED" ? "warn" : "ok"}`}>{p.status}</span></span>
            </div>
          ))}
        </div>
      </div>

      <div className="card">
        <div className="card-head">
          <div>
            <div className="card-title">{lang === "en" ? `Ledgers — Period ${periodId}` : `전표 · 기간 ${periodId}`}</div>
            <div className="card-sub">{lang === "en" ? `Debit ${fmtKRW(totalDr)} · Credit ${fmtKRW(totalCr)}` : `차변 ${fmtKRW(totalDr)} · 대변 ${fmtKRW(totalCr)}`}</div>
          </div>
          <button className="btn btn-sm">{lang === "en" ? "Verify balance" : "차·대변 검증"}</button>
        </div>
        <table className="tbl">
          <thead><tr>
            <th>ID</th><th>{lang === "en" ? "Type" : "유형"}</th><th>{lang === "en" ? "Reference" : "참조"}</th>
            <th>{lang === "en" ? "Description" : "내역"}</th><th className="num">{lang === "en" ? "Amount" : "금액"}</th><th>{lang === "en" ? "Created" : "발행일"}</th>
          </tr></thead>
          <tbody>
            {ledgers.map((l, i) => (
              <tr key={l.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                <td className="id">LDG-{l.id}</td>
                <td><span className={`badge ${TYPE[l.type]}`}>{l.type}</span></td>
                <td className="muted mono">#{l.referenceId}</td>
                <td>{l.description}</td>
                <td className="num strong" style={{ color: l.amount < 0 ? "var(--danger-ink)" : "inherit" }}>{l.amount > 0 ? "+" : ""}{fmtKRW(l.amount)}</td>
                <td className="muted">{fmtDateTime(l.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// CRM
// ============================================================
function CrmPage({ lang }) {
  const [tab, setTab] = uS3("customers");
  const GRADE = { VIP: "accent", GOLD: "warn", SILVER: "info", NORMAL: "neutral" };
  const CLAIM_STATUS = { OPEN: "danger", IN_PROGRESS: "warn", RESOLVED: "ok", CLOSED: "neutral" };
  return (
    <>
      <PageHead title={lang === "en" ? "Customers" : "고객 관리"}
        sub={lang === "en" ? `${CRM_CUSTOMERS.length} customers · ${CLAIMS.filter(c => c.status !== "RESOLVED" && c.status !== "CLOSED").length} open claims` : `고객 ${CRM_CUSTOMERS.length}명 · 미해결 클레임 ${CLAIMS.filter(c => c.status !== "RESOLVED" && c.status !== "CLOSED").length}건`}
        actions={
          <div style={{ display: "flex", gap: 0, border: "1px solid var(--border)", borderRadius: 6, overflow: "hidden" }}>
            <button className={`btn btn-sm ${tab === "customers" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("customers")}>{lang === "en" ? "Customers" : "고객"}</button>
            <button className={`btn btn-sm ${tab === "consult" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("consult")}>{lang === "en" ? "Consultations" : "상담"}</button>
            <button className={`btn btn-sm ${tab === "claims" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("claims")}>{lang === "en" ? "Claims" : "클레임"}</button>
          </div>
        } />

      {tab === "customers" && (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr>
              <th>{lang === "en" ? "Code" : "코드"}</th><th>{lang === "en" ? "Name" : "고객명"}</th><th>{lang === "en" ? "Contact" : "연락처"}</th>
              <th>{lang === "en" ? "Grade" : "등급"}</th><th className="num">{lang === "en" ? "Total Sales" : "누적 매출"}</th>
              <th className="num">{lang === "en" ? "Open Claims" : "클레임"}</th><th>{lang === "en" ? "Sales rep" : "담당"}</th>
            </tr></thead>
            <tbody>
              {CRM_CUSTOMERS.map((c, i) => (
                <tr key={c.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">{c.code}</td>
                  <td className="strong">{c.name}</td>
                  <td className="mono muted" style={{ fontSize: 11.5 }}>{c.contact}</td>
                  <td><span className={`badge ${GRADE[c.grade]}`}>{c.grade}</span></td>
                  <td className="num strong">{fmtKRW(c.totalSales)}</td>
                  <td className="num">{c.openClaims > 0 ? <span className="badge danger">{c.openClaims}</span> : <span className="muted">—</span>}</td>
                  <td className="muted">{c.assigned}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === "consult" && (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr><th>ID</th><th>{lang === "en" ? "Customer" : "고객"}</th><th>{lang === "en" ? "Type" : "유형"}</th><th>{lang === "en" ? "Title" : "제목"}</th><th>{lang === "en" ? "Handler" : "담당자"}</th><th>{lang === "en" ? "Created" : "일시"}</th></tr></thead>
            <tbody>
              {CONSULTATIONS.map((c, i) => (
                <tr key={c.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">CN-{c.id}</td>
                  <td className="strong">{lookupCustomer(c.customerId).name}</td>
                  <td><span className="badge info">{c.type}</span></td>
                  <td>{c.title}</td>
                  <td className="muted">{c.handler}</td>
                  <td className="muted">{fmtDateTime(c.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === "claims" && (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr><th>ID</th><th>{lang === "en" ? "Customer" : "고객"}</th><th>{lang === "en" ? "Title" : "제목"}</th><th>{lang === "en" ? "Priority" : "우선순위"}</th><th>{lang === "en" ? "Opened" : "접수"}</th><th>{lang === "en" ? "Status" : "상태"}</th></tr></thead>
            <tbody>
              {CLAIMS.map((c, i) => (
                <tr key={c.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">CL-{c.id}</td>
                  <td className="strong">{lookupCustomer(c.customerId).name}</td>
                  <td>{c.title}</td>
                  <td><span className={`badge ${c.priority === "HIGH" ? "danger" : c.priority === "MEDIUM" ? "warn" : "neutral"}`}>{c.priority}</span></td>
                  <td className="muted">{fmtDateTime(c.openedAt)}</td>
                  <td><span className={`badge ${CLAIM_STATUS[c.status]}`}>{c.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}

// ============================================================
// NOTIFICATIONS
// ============================================================
function NotificationsPage({ lang }) {
  const [filter, setFilter] = uS3("ALL");
  const items = filter === "ALL" ? NOTIFICATIONS : NOTIFICATIONS.filter(n => n.channel === filter);
  return (
    <>
      <PageHead title={lang === "en" ? "Notifications" : "알림"}
        sub={lang === "en" ? `${NOTIFICATIONS.length} notifications` : `총 ${NOTIFICATIONS.length}건`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "Send" : "수동 발송"}</button>} />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            {["ALL", "EMAIL", "SMS", "PUSH", "SYSTEM"].map(f => (
              <button key={f} className={`chip ${filter === f ? "on" : ""}`} onClick={() => setFilter(f)}>{f === "ALL" ? (lang === "en" ? "All" : "전체") : f}</button>
            ))}
          </div>
        </div>
        <table className="tbl">
          <thead><tr><th>ID</th><th>{lang === "en" ? "Channel" : "채널"}</th><th>{lang === "en" ? "Recipient" : "수신자"}</th><th>{lang === "en" ? "Title" : "제목"}</th><th>{lang === "en" ? "Body" : "내용"}</th><th>{lang === "en" ? "Sent" : "발송"}</th><th>{lang === "en" ? "Status" : "상태"}</th></tr></thead>
          <tbody>
            {items.map((n, i) => (
              <tr key={n.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                <td className="id">N-{n.id}</td>
                <td><span className="badge info">{n.channel}</span></td>
                <td className="muted">User-{n.recipientId}</td>
                <td className="strong">{n.title}</td>
                <td className="muted" style={{ maxWidth: 280, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{n.body}</td>
                <td className="muted">{fmtRelative(n.createdAt)}</td>
                <td><span className={`badge ${n.status === "SENT" ? "ok" : n.status === "FAILED" ? "danger" : "neutral"}`}>{n.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// REPORTS
// ============================================================
function ReportsPage({ lang }) {
  return (
    <>
      <PageHead title={lang === "en" ? "Reports" : "리포트"}
        sub={lang === "en" ? "Daily / weekly / monthly snapshots" : "일별 · 주별 · 월별 스냅샷"}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "Generate" : "생성"}</button>} />

      <div className="grid-3" style={{ marginBottom: 14 }}>
        {REPORTS.map(r => (
          <div className="card" key={r.id} style={{ padding: 18 }}>
            <div className="muted" style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: "0.04em", textTransform: "uppercase" }}>{r.reportType.replace(/_/g, " ")}</div>
            <div style={{ fontSize: 13, color: "var(--text-3)", marginTop: 4 }}>{r.targetDate}</div>
            <div style={{ marginTop: 14, display: "flex", flexDirection: "column", gap: 8 }}>
              {Object.entries(r.metrics).map(([k, v]) => (
                <div key={k} style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", borderBottom: "1px solid var(--divider)", paddingBottom: 4 }}>
                  <span className="muted" style={{ fontSize: 12 }}>{k}</span>
                  <span className="strong tnum" style={{ fontSize: 14 }}>{typeof v === "number" && v >= 1000 ? fmtN(v) : v}</span>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </>
  );
}

// ============================================================
// APPROVALS
// ============================================================
function ApprovalsPage({ lang }) {
  const [tab, setTab] = uS3("inbox");
  const STATUS = { IN_PROGRESS: "warn", APPROVED: "ok", REJECTED: "danger", CANCELLED: "neutral" };
  const items = tab === "inbox" ? APPROVALS.filter(a => a.status === "IN_PROGRESS") :
                tab === "drafted" ? APPROVALS.filter(a => a.drafterId === 1) : APPROVALS;
  return (
    <>
      <PageHead title={lang === "en" ? "Approvals" : "전자결재"}
        sub={lang === "en" ? `${APPROVALS.filter(a => a.status === "IN_PROGRESS").length} pending` : `진행중 ${APPROVALS.filter(a => a.status === "IN_PROGRESS").length}건`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "Draft" : "기안"}</button>} />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            <button className={`chip ${tab === "inbox" ? "on" : ""}`} onClick={() => setTab("inbox")}>{lang === "en" ? "Inbox" : "결재함"}</button>
            <button className={`chip ${tab === "drafted" ? "on" : ""}`} onClick={() => setTab("drafted")}>{lang === "en" ? "Drafted" : "내가 기안한"}</button>
            <button className={`chip ${tab === "all" ? "on" : ""}`} onClick={() => setTab("all")}>{lang === "en" ? "All" : "전체"}</button>
          </div>
        </div>
        <table className="tbl">
          <thead><tr><th>ID</th><th>{lang === "en" ? "Type" : "유형"}</th><th>{lang === "en" ? "Title" : "제목"}</th><th>{lang === "en" ? "Drafter" : "기안자"}</th><th>{lang === "en" ? "Step" : "단계"}</th><th>{lang === "en" ? "Created" : "기안일"}</th><th>{lang === "en" ? "Status" : "상태"}</th></tr></thead>
          <tbody>
            {items.map((a, i) => (
              <tr key={a.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                <td className="id">AP-{a.id}</td>
                <td><span className="badge info">{a.documentType}</span></td>
                <td className="strong">{a.title}</td>
                <td className="muted">{a.drafterName}</td>
                <td className="tnum muted">{a.currentStep}/{a.totalSteps}</td>
                <td className="muted">{fmtDateTime(a.createdAt)}</td>
                <td><span className={`badge ${STATUS[a.status]}`}>{a.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// HR
// ============================================================
function HrPage({ lang }) {
  const [tab, setTab] = uS3("employees");
  return (
    <>
      <PageHead title={lang === "en" ? "Human Resources" : "인사 / 급여"}
        sub={lang === "en" ? `${EMPLOYEES.length} employees · payroll for April` : `직원 ${EMPLOYEES.length}명 · 4월 급여`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "Hire" : "입사 등록"}</button>} />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            <button className={`chip ${tab === "employees" ? "on" : ""}`} onClick={() => setTab("employees")}>{lang === "en" ? "Employees" : "직원"}</button>
            <button className={`chip ${tab === "payroll" ? "on" : ""}`} onClick={() => setTab("payroll")}>{lang === "en" ? "Payroll" : "급여"}</button>
          </div>
        </div>
        {tab === "employees" ? (
          <table className="tbl">
            <thead><tr><th>{lang === "en" ? "Number" : "사번"}</th><th>{lang === "en" ? "Name" : "이름"}</th><th>{lang === "en" ? "Department" : "부서"}</th><th>{lang === "en" ? "Position" : "직급"}</th><th>{lang === "en" ? "Joined" : "입사일"}</th><th className="num">{lang === "en" ? "Base salary" : "기본급"}</th><th>{lang === "en" ? "Status" : "상태"}</th></tr></thead>
            <tbody>
              {EMPLOYEES.map((e, i) => (
                <tr key={e.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">{e.employeeNumber}</td>
                  <td className="strong">{e.name}</td>
                  <td>{e.department}</td>
                  <td className="muted">{e.position}</td>
                  <td className="muted">{e.joinedAt}</td>
                  <td className="num strong">{fmtKRW(e.baseSalary)}</td>
                  <td><span className="badge ok">{e.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <table className="tbl">
            <thead><tr><th>ID</th><th>{lang === "en" ? "Employee" : "직원"}</th><th>{lang === "en" ? "Period" : "기간"}</th><th className="num">{lang === "en" ? "Base" : "기본급"}</th><th className="num">{lang === "en" ? "Allowance" : "수당"}</th><th className="num">{lang === "en" ? "Insurance" : "공제"}</th><th className="num">{lang === "en" ? "Net" : "실수령"}</th></tr></thead>
            <tbody>
              {PAYROLLS.map((p, i) => {
                const e = EMPLOYEES.find(x => x.id === p.employeeId);
                return (
                  <tr key={p.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                    <td className="id">PR-{p.id}</td>
                    <td className="strong">{e?.name}</td>
                    <td className="muted">{p.year}-{String(p.month).padStart(2, "0")}</td>
                    <td className="num muted">{fmtKRW(p.baseSalary)}</td>
                    <td className="num muted">+{fmtKRW(p.allowance)}</td>
                    <td className="num muted" style={{ color: "var(--danger-ink)" }}>−{fmtKRW(p.insurance)}</td>
                    <td className="num strong">{fmtKRW(p.netSalary)}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}

// ============================================================
// SUPPLIERS
// ============================================================
function SuppliersPage({ lang }) {
  return (
    <>
      <PageHead title={lang === "en" ? "Suppliers" : "공급자"}
        sub={lang === "en" ? `${SUPPLIERS.length} suppliers` : `총 ${SUPPLIERS.length}곳`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "Add supplier" : "공급자 등록"}</button>} />
      <div className="tbl-wrap">
        <table className="tbl">
          <thead><tr><th>{lang === "en" ? "Code" : "코드"}</th><th>{lang === "en" ? "Name" : "공급자명"}</th><th>{lang === "en" ? "Category" : "카테고리"}</th><th>{lang === "en" ? "Contact" : "연락처"}</th><th>{lang === "en" ? "Grade" : "등급"}</th></tr></thead>
          <tbody>
            {SUPPLIERS.map((s, i) => (
              <tr key={s.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                <td className="id">{s.code}</td>
                <td className="strong">{s.name}</td>
                <td>{s.category}</td>
                <td className="mono muted" style={{ fontSize: 11.5 }}>{s.contact}</td>
                <td><span className={`badge ${s.grade === "A" ? "ok" : "warn"}`}>{s.grade}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// PRODUCTION
// ============================================================
function ProductionPage({ lang }) {
  const STATUS = { PLANNED: "neutral", IN_PROGRESS: "warn", COMPLETED: "ok", CANCELLED: "danger" };
  return (
    <>
      <PageHead title={lang === "en" ? "Production" : "생산 관리"}
        sub={lang === "en" ? `${WORK_ORDERS.length} work orders` : `작업지시 ${WORK_ORDERS.length}건`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "Issue" : "작업지시 발행"}</button>} />
      <div className="tbl-wrap">
        <table className="tbl">
          <thead><tr><th>ID</th><th>{lang === "en" ? "Product" : "상품"}</th><th className="num">{lang === "en" ? "Planned" : "계획"}</th><th className="num">{lang === "en" ? "Produced" : "실적"}</th><th className="num">{lang === "en" ? "Defective" : "불량"}</th><th style={{ width: 200 }}>{lang === "en" ? "Progress" : "진척률"}</th><th>{lang === "en" ? "Status" : "상태"}</th></tr></thead>
          <tbody>
            {WORK_ORDERS.map((w, i) => {
              const pct = w.plannedQuantity > 0 ? (w.produced / w.plannedQuantity) * 100 : 0;
              return (
                <tr key={w.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">WO-{w.id}</td>
                  <td className="strong">{lookupProduct(w.productId).name}</td>
                  <td className="num">{w.plannedQuantity}</td>
                  <td className="num strong">{w.produced}</td>
                  <td className="num muted">{w.defective}</td>
                  <td><div className="pbar"><div className="pbar-fill" style={{ width: `${pct}%` }} /></div></td>
                  <td><span className={`badge ${STATUS[w.status]}`}>{w.status}</span></td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// PURCHASE ORDER CREATE PANEL
// ============================================================
function PurchaseOrderCreatePanel({ onClose, lang }) {
  const [supplier, setSupplier] = uS3(SUPPLIERS[0].id);
  const [productId, setProductId] = uS3(PRODUCTS[0].id);
  const [qty, setQty] = uS3(50);
  const [unitPrice, setUnitPrice] = uS3(500_000);
  const toast = useToast();
  const submit = () => { toast.push(lang === "en" ? "Purchase order issued" : "발주 등록 완료"); onClose(); };
  return (
    <>
      <div className="sp-head">
        <div>
          <div className="sp-title">{lang === "en" ? "New Purchase Order" : "발주 등록"}</div>
          <div className="sp-sub">{lang === "en" ? "Issue PO to supplier" : "공급자에게 발주서 발행"}</div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>
      <div className="sp-body">
        <div>
          <div className="section-h">{lang === "en" ? "Supplier" : "공급자"}</div>
          <div className="field">
            <select className="input" value={supplier} onChange={e => setSupplier(Number(e.target.value))}>
              {SUPPLIERS.map(s => <option key={s.id} value={s.id}>{s.name} · {s.category}</option>)}
            </select>
          </div>
        </div>
        <div>
          <div className="section-h">{lang === "en" ? "Item" : "품목"}</div>
          <div className="grid-2" style={{ gap: 10 }}>
            <div className="field">
              <label className="field-label">{lang === "en" ? "Product" : "상품"}</label>
              <select className="input" value={productId} onChange={e => setProductId(Number(e.target.value))}>
                {PRODUCTS.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
              </select>
            </div>
            <div className="field">
              <label className="field-label">{lang === "en" ? "Quantity" : "수량"}</label>
              <input type="number" className="input" value={qty} onChange={e => setQty(Number(e.target.value))} />
            </div>
            <div className="field">
              <label className="field-label">{lang === "en" ? "Unit price" : "단가 (₩)"}</label>
              <input type="number" className="input" value={unitPrice} onChange={e => setUnitPrice(Number(e.target.value))} step={1000} />
            </div>
            <div className="field">
              <label className="field-label">{lang === "en" ? "Total" : "총액"}</label>
              <div className="input" style={{ display: "flex", alignItems: "center", fontWeight: 600 }}>{fmtKRW(qty * unitPrice)}</div>
            </div>
          </div>
        </div>
        <div>
          <div className="section-h">{lang === "en" ? "Notes" : "비고"}</div>
          <textarea className="input" style={{ height: 80, padding: 10, resize: "vertical" }} placeholder={lang === "en" ? "Optional notes..." : "메모 (선택)"} />
        </div>
      </div>
      <div className="sp-foot">
        <button className="btn" onClick={onClose}>{lang === "en" ? "Cancel" : "취소"}</button>
        <button className="btn btn-accent" onClick={submit}>{lang === "en" ? "Issue PO" : "발주 발행"}</button>
      </div>
    </>
  );
}

Object.assign(window, {
  QuotesPage, PaymentsPage, SettlementPage, CrmPage, NotificationsPage, ReportsPage,
  ApprovalsPage, HrPage, SuppliersPage, ProductionPage, PurchaseOrderCreatePanel,
});
