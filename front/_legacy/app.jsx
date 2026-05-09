/* global React, ReactDOM */
/* global ToastProvider, useTweaks, TweaksPanel, TweakSection, TweakRadio, TweakToggle, TweakColor, TweakSelect */
/* global DashboardPage, OrdersPage, OrderPanel, ShipmentsPage, ShipmentPanel, DeliveriesPage, StocksPage, PurchaseOrdersPage */
/* global QuotesPage, PaymentsPage, SettlementPage, CrmPage, NotificationsPage, ReportsPage, ApprovalsPage, HrPage, SuppliersPage, ProductionPage, PurchaseOrderCreatePanel */
/* global ExtStoreProvider, PaymentsPageV2, SettlementPageV2, ApprovalsPageV2, CrmPageV2, ApprovalPanel, CustomerPanel */
/* global QuotesPageV2, PurchaseOrdersPageV2, SuppliersPageV2 */
/* global SidePanel, Icon */
/* global initialStocks, initialOrders, initialShipments, initialDeliveries, initialPurchaseOrders */

const { useState: us, useEffect: ue, useMemo: um } = React;

const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "density": "regular",
  "dark": false,
  "lang": "ko",
  "accent": "#5a9b7d"
}/*EDITMODE-END*/;

const NAV = [
  { section: "메인", en: "Main", items: [
    { key: "dashboard",     ko: "대시보드",  en: "Dashboard",      icon: "Dashboard" },
  ]},
  { section: "영업", en: "Sales", items: [
    { key: "quotes",        ko: "견적",      en: "Quotes",         icon: "Doc"   },
    { key: "orders",        ko: "수주",      en: "Sales Orders",   icon: "Cart"  },
    { key: "payments",      ko: "결제",      en: "Payments",       icon: "Card"  },
    { key: "crm",           ko: "고객",      en: "Customers",      icon: "Users" },
  ]},
  { section: "물류", en: "Logistics", items: [
    { key: "shipments",     ko: "출고",      en: "Shipments",      icon: "Truck" },
    { key: "deliveries",    ko: "배송 추적", en: "Deliveries",     icon: "Route" },
  ]},
  { section: "공급망", en: "Supply", items: [
    { key: "stocks",        ko: "재고",      en: "Stocks",         icon: "Box"   },
    { key: "purchase",      ko: "발주",      en: "Purchase Orders",icon: "DocCheck" },
    { key: "suppliers",     ko: "공급자",    en: "Suppliers",      icon: "Building" },
    { key: "production",    ko: "생산",      en: "Production",     icon: "Factory" },
  ]},
  { section: "재무", en: "Finance", items: [
    { key: "settlement",    ko: "정산",      en: "Settlement",     icon: "Coin"  },
  ]},
  { section: "운영", en: "Operations", items: [
    { key: "approvals",     ko: "전자결재",  en: "Approvals",      icon: "Check" },
    { key: "notifications", ko: "알림",      en: "Notifications",  icon: "Bell"  },
    { key: "reports",       ko: "리포트",    en: "Reports",        icon: "Chart" },
    { key: "hr",            ko: "인사",      en: "HR",             icon: "User"  },
  ]},
];

function App() {
  const [t, setTweak] = useTweaks(TWEAK_DEFAULTS);
  const lang = t.lang;

  // root attrs for theming
  ue(() => {
    document.documentElement.dataset.density = t.density;
    document.documentElement.dataset.theme = t.dark ? "dark" : "light";
    document.documentElement.style.setProperty("--accent", t.accent);
  }, [t.density, t.dark, t.accent]);

  const [page, setPage] = us("dashboard");
  const [store, setStore] = us({
    orders: initialOrders,
    shipments: initialShipments,
    deliveries: initialDeliveries,
    stocks: initialStocks,
    purchaseOrders: initialPurchaseOrders,
  });

  const [openOrder, setOpenOrder] = us(null);
  const [openShipment, setOpenShipment] = us(null);
  const [openPoCreate, setOpenPoCreate] = us(false);
  const [openApproval, setOpenApproval] = us(null);
  const [openCustomer, setOpenCustomer] = us(null);

  // Counts for sidebar badges
  const counts = um(() => ({
    orders: store.orders.filter((o) => !o.paid).length,
    shipments: store.shipments.filter((s) => s.status === "PREPARING").length,
    deliveries: store.deliveries.filter((d) => d.status === "IN_TRANSIT" || d.status === "ASSIGNED").length,
    stocks: store.stocks.filter((s) => (s.total - s.reserved) <= 10).length,
    purchase: store.purchaseOrders.filter((p) => p.status === "ISSUED" || p.status === "PARTIAL").length,
    quotes: QUOTES.filter((q) => q.status === "ACTIVE").length,
    payments: PAYMENTS.filter((p) => p.status === "PENDING").length,
    approvals: APPROVALS.filter((a) => a.status === "IN_PROGRESS").length,
    notifications: NOTIFICATIONS.filter((n) => n.status === "FAILED").length,
    crm: CLAIMS.filter((c) => c.status === "OPEN").length,
  }), [store]);

  const crumb = um(() => {
    for (const s of NAV) {
      const it = s.items.find((i) => i.key === page);
      if (it) return { section: lang === "en" ? s.en : s.section, page: lang === "en" ? it.en : it.ko };
    }
    return { section: "", page: "" };
  }, [page, lang]);

  const renderPage = () => {
    switch (page) {
      case "dashboard":  return <DashboardPage store={store} lang={lang} />;
      case "orders":     return <OrdersPage store={store} setStore={setStore} lang={lang} onOpenOrder={setOpenOrder} />;
      case "shipments":  return <ShipmentsPage store={store} setStore={setStore} lang={lang} onOpen={setOpenShipment} />;
      case "deliveries": return <DeliveriesPage store={store} setStore={setStore} lang={lang} />;
      case "stocks":     return <StocksPage store={store} lang={lang} />;
      case "purchase":   return <PurchaseOrdersPageV2 store={store} setStore={setStore} lang={lang} onCreate={() => setOpenPoCreate(true)} />;
      case "quotes":     return <QuotesPageV2 store={store} setStore={setStore} lang={lang} onConverted={(id) => { setPage("orders"); setOpenOrder(id); }} />;
      case "payments":   return <PaymentsPageV2 lang={lang} />;
      case "settlement": return <SettlementPageV2 lang={lang} />;
      case "crm":        return <CrmPageV2 lang={lang} onOpenCustomer={setOpenCustomer} />;
      case "notifications": return <NotificationsPage lang={lang} />;
      case "reports":    return <ReportsPage lang={lang} />;
      case "approvals":  return <ApprovalsPageV2 lang={lang} onOpen={setOpenApproval} />;
      case "hr":         return <HrPage lang={lang} />;
      case "suppliers":  return <SuppliersPageV2 lang={lang} />;
      case "production": return <ProductionPage lang={lang} />;
      default: return null;
    }
  };

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark" />
          <div>
            <div className="brand-name">Logiform</div>
            <div className="brand-sub">{lang === "en" ? "Logistics ERP" : "물류 ERP"}</div>
          </div>
        </div>
        <nav className="nav">
          {NAV.map((s) => (
            <React.Fragment key={s.section}>
              <div className="nav-section">{lang === "en" ? s.en : s.section}</div>
              {s.items.map((it) => {
                const Ico = Icon[it.icon];
                const cnt = counts[it.key];
                return (
                  <div key={it.key}
                    className={`nav-item ${page === it.key ? "active" : ""}`}
                    onClick={() => setPage(it.key)}
                    data-screen-label={`${it.key}`}>
                    <span className="ico"><Ico /></span>
                    <span>{lang === "en" ? it.en : it.ko}</span>
                    {cnt > 0 && <span className="badge">{cnt}</span>}
                  </div>
                );
              })}
            </React.Fragment>
          ))}
        </nav>
        <div className="nav-footer">
          <div className="avatar">JS</div>
          <div>
            <div className="user-name">{lang === "en" ? "Jun Seo" : "서준"}</div>
            <div className="user-role">{lang === "en" ? "Logistics Lead" : "물류팀 · 매니저"}</div>
          </div>
        </div>
      </aside>

      <main className="main">
        <div className="topbar">
          <div className="crumbs">
            <span>{crumb.section}</span>
            <span className="sep">/</span>
            <span className="current">{crumb.page}</span>
          </div>
          <div className="search">
            <span className="ico"><Icon.Search /></span>
            <input placeholder={lang === "en" ? "Search anything..." : "주문·고객·상품·송장 통합 검색"} />
            <kbd>⌘K</kbd>
          </div>
          <button className="icon-btn"><Icon.Bell /><span className="dot" /></button>
          <button className="icon-btn"><Icon.Settings /></button>
        </div>
        <div className="page">{renderPage()}</div>
      </main>

      <SidePanel open={!!openOrder} onClose={() => setOpenOrder(null)}>
        {openOrder && <OrderPanel orderId={openOrder} store={store} setStore={setStore} onClose={() => setOpenOrder(null)} lang={lang} />}
      </SidePanel>
      <SidePanel open={!!openShipment} onClose={() => setOpenShipment(null)}>
        {openShipment && <ShipmentPanel shipmentId={openShipment} store={store} setStore={setStore} onClose={() => setOpenShipment(null)} lang={lang} />}
      </SidePanel>
      <SidePanel open={openPoCreate} onClose={() => setOpenPoCreate(false)}>
        {openPoCreate && <PurchaseOrderCreatePanel onClose={() => setOpenPoCreate(false)} lang={lang} />}
      </SidePanel>
      <SidePanel open={!!openApproval} onClose={() => setOpenApproval(null)}>
        {openApproval && <ApprovalPanel approvalId={openApproval} onClose={() => setOpenApproval(null)} lang={lang} />}
      </SidePanel>
      <SidePanel open={!!openCustomer} onClose={() => setOpenCustomer(null)}>
        {openCustomer && <CustomerPanel customerId={openCustomer} store={store} onClose={() => setOpenCustomer(null)} lang={lang} />}
      </SidePanel>

      <TweaksPanel title="Tweaks">
        <TweakSection label="Display" />
        <TweakRadio label="Density" value={t.density} options={["compact", "regular", "comfy"]}
                    onChange={(v) => setTweak("density", v)} />
        <TweakToggle label="Dark mode" value={t.dark} onChange={(v) => setTweak("dark", v)} />
        <TweakSection label="Locale" />
        <TweakRadio label="Language" value={t.lang} options={[{ value: "ko", label: "한국어" }, { value: "en", label: "English" }]}
                    onChange={(v) => setTweak("lang", v)} />
        <TweakSection label="Accent" />
        <TweakColor label="Color" value={t.accent} onChange={(v) => setTweak("accent", v)} />
      </TweaksPanel>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(
  <ToastProvider><ExtStoreProvider><App /></ExtStoreProvider></ToastProvider>
);
