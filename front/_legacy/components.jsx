/* global React */
// Icons + small reusable components

const ICO_PROPS = { width: 16, height: 16, viewBox: "0 0 16 16", fill: "none", stroke: "currentColor", strokeWidth: 1.5, strokeLinecap: "round", strokeLinejoin: "round" };

const Icon = {
  Dashboard:  () => <svg {...ICO_PROPS}><rect x="2" y="2" width="5" height="5" rx="1"/><rect x="9" y="2" width="5" height="5" rx="1"/><rect x="2" y="9" width="5" height="5" rx="1"/><rect x="9" y="9" width="5" height="5" rx="1"/></svg>,
  Cart:       () => <svg {...ICO_PROPS}><path d="M2 3h2l2 8h8l1.5-6H5"/><circle cx="6.5" cy="13.5" r="0.8"/><circle cx="12.5" cy="13.5" r="0.8"/></svg>,
  Truck:      () => <svg {...ICO_PROPS}><path d="M1.5 4h8v8h-8z"/><path d="M9.5 7h3l2 2v3h-5"/><circle cx="4" cy="13" r="1.2"/><circle cx="12" cy="13" r="1.2"/></svg>,
  Route:      () => <svg {...ICO_PROPS}><circle cx="3" cy="3" r="1.5"/><circle cx="13" cy="13" r="1.5"/><path d="M3 4.5v3a3 3 0 0 0 3 3h4a3 3 0 0 1 3 3"/></svg>,
  Box:        () => <svg {...ICO_PROPS}><path d="M2 5l6-3 6 3v6l-6 3-6-3z"/><path d="M2 5l6 3 6-3M8 8v6"/></svg>,
  Doc:        () => <svg {...ICO_PROPS}><path d="M3 2h7l3 3v9H3z"/><path d="M10 2v3h3"/><path d="M5 8h6M5 11h4"/></svg>,
  Search:     () => <svg {...ICO_PROPS}><circle cx="7" cy="7" r="4.5"/><path d="m10.5 10.5 3 3"/></svg>,
  Bell:       () => <svg {...ICO_PROPS}><path d="M4 6a4 4 0 0 1 8 0v3l1.5 2H2.5L4 9z"/><path d="M6.5 13a1.5 1.5 0 0 0 3 0"/></svg>,
  Plus:       () => <svg {...ICO_PROPS}><path d="M8 3v10M3 8h10"/></svg>,
  Filter:     () => <svg {...ICO_PROPS}><path d="M2 3h12l-4.5 6v4l-3 1V9z"/></svg>,
  Download:   () => <svg {...ICO_PROPS}><path d="M8 2v8m-3-3 3 3 3-3M3 12h10"/></svg>,
  ChevronR:   () => <svg {...ICO_PROPS}><path d="m6 3 5 5-5 5"/></svg>,
  ChevronD:   () => <svg {...ICO_PROPS}><path d="m3 6 5 5 5-5"/></svg>,
  ArrowUp:    () => <svg {...ICO_PROPS}><path d="m4 9 4-4 4 4M8 5v9"/></svg>,
  ArrowDown:  () => <svg {...ICO_PROPS}><path d="m4 7 4 4 4-4M8 11V2"/></svg>,
  Check:      () => <svg {...ICO_PROPS}><path d="m3 8 3 3 7-7"/></svg>,
  X:          () => <svg {...ICO_PROPS}><path d="M3 3l10 10M13 3 3 13"/></svg>,
  More:       () => <svg {...ICO_PROPS}><circle cx="3" cy="8" r="1"/><circle cx="8" cy="8" r="1"/><circle cx="13" cy="8" r="1"/></svg>,
  Settings:   () => <svg {...ICO_PROPS}><circle cx="8" cy="8" r="2"/><path d="M8 1.5v2M8 12.5v2M14.5 8h-2M3.5 8h-2m11-4.5L12 5m-8 6-1.5 1.5m10 0L12 11m-8-6L2.5 3.5"/></svg>,
  Refresh:    () => <svg {...ICO_PROPS}><path d="M2 8a6 6 0 0 1 11-3.5M14 8a6 6 0 0 1-11 3.5"/><path d="M11 1v3h3M5 15v-3H2"/></svg>,
  Pin:        () => <svg {...ICO_PROPS}><path d="M8 14v-3M5 3l1 6h4l1-6z"/><path d="M4 3h8"/></svg>,
  Map:        () => <svg {...ICO_PROPS}><path d="M2 4l4-2 4 2 4-2v10l-4 2-4-2-4 2z"/><path d="M6 2v10M10 4v10"/></svg>,
  Clock:      () => <svg {...ICO_PROPS}><circle cx="8" cy="8" r="6"/><path d="M8 4.5V8l2.5 1.5"/></svg>,
  Warn:       () => <svg {...ICO_PROPS}><path d="M8 2 14 13H2z"/><path d="M8 6.5v3M8 11.5v.01"/></svg>,
  Receive:    () => <svg {...ICO_PROPS}><path d="M2 13h12M8 2v8m-3-3 3 3 3-3"/></svg>,
  Tag:        () => <svg {...ICO_PROPS}><path d="M2 8 8 2h5v5l-6 6z"/><circle cx="10.5" cy="5.5" r="0.6"/></svg>,
  Card:       () => <svg {...ICO_PROPS}><rect x="1.5" y="3.5" width="13" height="9" rx="1.5"/><path d="M1.5 6.5h13M3.5 10h3"/></svg>,
  Users:      () => <svg {...ICO_PROPS}><circle cx="6" cy="6" r="2.5"/><path d="M2 13a4 4 0 0 1 8 0"/><circle cx="11.5" cy="6" r="2"/><path d="M10 13a3.5 3.5 0 0 1 4-3.5"/></svg>,
  User:       () => <svg {...ICO_PROPS}><circle cx="8" cy="6" r="2.5"/><path d="M3 13.5a5 5 0 0 1 10 0"/></svg>,
  DocCheck:   () => <svg {...ICO_PROPS}><path d="M3 2h7l3 3v9H3z"/><path d="M10 2v3h3"/><path d="m5.5 9.5 1.5 1.5 3-3"/></svg>,
  Building:   () => <svg {...ICO_PROPS}><rect x="2.5" y="2.5" width="11" height="11" rx="0.5"/><path d="M5 5h1.5M5 7.5h1.5M5 10h1.5M9.5 5H11M9.5 7.5H11M9.5 10H11"/></svg>,
  Factory:    () => <svg {...ICO_PROPS}><path d="M2 13.5V7l3 1.5V7l3 1.5V7l3 1.5V4.5h2v9z"/><path d="M2 13.5h12"/></svg>,
  Coin:       () => <svg {...ICO_PROPS}><circle cx="8" cy="8" r="6"/><path d="M8 4.5v7M6 6h3a1.5 1.5 0 0 1 0 3H6h3a1.5 1.5 0 0 1 0 3H6"/></svg>,
  Chart:      () => <svg {...ICO_PROPS}><path d="M2 13.5h12M4 11V7M7.5 11V4M11 11V8.5"/></svg>,
};

// ─── Status badge ─────────────────────────────────────────────────
function StatusBadge({ status, live, lang = "ko" }) {
  const meta = STATUS_META[status] || { label: status, en: status, tone: "neutral" };
  const cls = `badge ${meta.tone}${live ? " live" : ""}`;
  return <span className={cls}>{lang === "en" ? meta.en : meta.label}</span>;
}

// ─── Stepper ──────────────────────────────────────────────────────
function Stepper({ steps, current, lang = "ko" }) {
  const idx = steps.indexOf(current);
  return (
    <div className="stepper">
      {steps.map((s, i) => {
        const meta = STATUS_META[s] || { label: s, en: s };
        const cls = i < idx ? "step done" : i === idx ? "step now" : "step";
        return (
          <React.Fragment key={s}>
            <div className={cls}>
              <div className="dot">
                {i < idx ? "✓" : i + 1}
              </div>
              <div className="label">{lang === "en" ? meta.en : meta.label}</div>
            </div>
            {i < steps.length - 1 && <div className="bar" style={{ background: i < idx ? "var(--accent)" : "var(--border)" }} />}
          </React.Fragment>
        );
      })}
    </div>
  );
}

// ─── KPI Card ─────────────────────────────────────────────────────
function KPI({ label, value, unit, delta, vs, format = "n", spark }) {
  const animated = useCountUp(value);
  const display = format === "krw"
    ? "₩" + Math.round(animated).toLocaleString("ko-KR")
    : Math.round(animated).toLocaleString("ko-KR");
  return (
    <div className="kpi">
      {spark && <Sparkline points={spark} />}
      <div className="kpi-label">{label}</div>
      <div className="kpi-num">
        <span>{display}</span>
        {unit && <span className="kpi-unit">{unit}</span>}
      </div>
      {(delta != null) && (
        <div className="kpi-meta">
          <span className={`kpi-delta ${delta >= 0 ? "up" : "down"}`}>
            {delta >= 0 ? "▲" : "▼"} {Math.abs(delta).toFixed(1)}%
          </span>
          {vs && <span className="vs">{vs}</span>}
        </div>
      )}
    </div>
  );
}

function Sparkline({ points }) {
  if (!points || !points.length) return null;
  const w = 76, h = 28;
  const min = Math.min(...points), max = Math.max(...points);
  const norm = (v) => h - 2 - ((v - min) / (max - min || 1)) * (h - 4);
  const step = w / (points.length - 1);
  const d = points.map((p, i) => `${i === 0 ? "M" : "L"} ${i * step} ${norm(p)}`).join(" ");
  return (
    <svg className="spark" viewBox={`0 0 ${w} ${h}`}>
      <path d={d} fill="none" stroke="var(--accent)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" opacity="0.85" />
      <circle cx={(points.length - 1) * step} cy={norm(points[points.length - 1])} r="2" fill="var(--accent)" />
    </svg>
  );
}

// ─── Skeleton row ─────────────────────────────────────────────────
function SkelRow({ cols = 6 }) {
  return (
    <tr>
      {Array.from({ length: cols }).map((_, i) => (
        <td key={i}><span className="skel" style={{ width: `${40 + ((i * 17) % 50)}%` }} /></td>
      ))}
    </tr>
  );
}

// ─── Side Panel ──────────────────────────────────────────────────
function SidePanel({ open, onClose, children }) {
  useEffect(() => {
    if (!open) return;
    const onEsc = (e) => { if (e.key === "Escape") onClose(); };
    window.addEventListener("keydown", onEsc);
    return () => window.removeEventListener("keydown", onEsc);
  }, [open, onClose]);
  if (!open) return null;
  return (
    <>
      <div className="scrim" onClick={onClose} />
      <aside className="side-panel" role="dialog">
        {children}
      </aside>
    </>
  );
}

// ─── Page Title ──────────────────────────────────────────────────
function PageHead({ title, sub, actions }) {
  return (
    <div className="page-head">
      <div>
        <h1 className="page-title">{title}</h1>
        {sub && <div className="page-sub">{sub}</div>}
      </div>
      {actions && <div className="page-actions">{actions}</div>}
    </div>
  );
}

Object.assign(window, {
  Icon, StatusBadge, Stepper, KPI, Sparkline, SkelRow, SidePanel, PageHead,
});
