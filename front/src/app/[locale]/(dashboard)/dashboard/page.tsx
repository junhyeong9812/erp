import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/routing";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { Badge } from "@/components/ui/badge";
import { PageHead } from "@/components/erp/page-head";
import { Kpi } from "@/components/erp/kpi";
import { Stepper } from "@/components/erp/stepper";
import { StatusBadge } from "@/components/erp/status-badge";
import {
  initialOrders,
  initialShipments,
  initialStocks,
  APPROVALS,
  NOTIFICATIONS,
  lookupCustomer,
  lookupWarehouse,
  lookupProduct,
} from "@/lib/mock";
import { lookupEmployee } from "@/lib/mock/hr";
import { lookupAppUser } from "@/lib/mock/auth";
import { fmtKRW, fmtN, fmtDateTime } from "@/lib/format";
import { SHIPMENT_STEPS } from "@/lib/types";
import { severityOf } from "@/features/stocks/severity";

const TODAY = "2026-05-04";

export default async function DashboardPage() {
  const tPage = await getTranslations("Pages.dashboard");
  const tCommon = await getTranslations("Common");
  const tDb = await getTranslations("Dashboard");

  // KPI 데이터
  const todays = initialOrders.filter((o) => o.placedAt.startsWith(TODAY));
  const todaySales = todays
    .filter((o) => o.paid)
    .reduce((sum, o) => sum + o.total, 0);
  const inTransitCount = initialShipments.filter(
    (s) => s.status === "DISPATCHED" || s.status === "PREPARING"
  ).length;
  const lowStockCount = initialStocks.filter(
    (s) => s.total - s.reserved <= 10
  ).length;

  // 진행중 출고
  const activeShipments = initialShipments.filter(
    (s) => s.status !== "COMPLETED"
  );

  // 최근 주문 5건
  const recentOrders = [...initialOrders]
    .sort((a, b) => (a.placedAt < b.placedAt ? 1 : -1))
    .slice(0, 5);

  // 위험 SKU
  const criticalStocks = initialStocks
    .map((s) => ({
      ...s,
      available: s.total - s.reserved,
      severity: severityOf(s.total - s.reserved),
    }))
    .filter((s) => s.severity === "critical")
    .sort((a, b) => a.available - b.available);

  // 결재 대기
  const openApprovals = APPROVALS.filter(
    (a) => a.status === "IN_PROGRESS"
  );

  // 최근 알림 5건
  const recentNotifications = [...NOTIFICATIONS]
    .sort((a, b) => {
      const aT = a.sentAt ?? "1970-01-01";
      const bT = b.sentAt ?? "1970-01-01";
      return aT < bT ? 1 : -1;
    })
    .slice(0, 5);

  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tPage("subtitle")}
        actions={
          <>
            <Button variant="outline" size="sm">
              <Icon.Refresh className="size-3.5" />
              {tCommon("refresh")}
            </Button>
            <Button variant="outline" size="sm">
              <Icon.Download className="size-3.5" />
              {tCommon("export")}
            </Button>
          </>
        }
      />

      {/* KPI 4 */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <Kpi
          label={tDb("kpi.todaySales")}
          value={todaySales}
          format="krw"
          delta={12.4}
          vs={tCommon("vsYesterday")}
          spark={[18, 22, 20, 28, 26, 33, 38, 36, 42]}
        />
        <Kpi
          label={tDb("kpi.ordersToday")}
          value={todays.length}
          unit={tDb("unit.orders")}
          delta={4.2}
          vs={tCommon("vsYesterday")}
          spark={[5, 6, 8, 7, 9, 10, 8, 12, 14]}
        />
        <Kpi
          label={tDb("kpi.inTransit")}
          value={inTransitCount}
          unit={tDb("unit.deliveries")}
          delta={-1.1}
          vs={tCommon("vsAvg")}
          spark={[4, 5, 4, 6, 5, 7, 6, 5, 4]}
        />
        <Kpi
          label={tDb("kpi.lowStock")}
          value={lowStockCount}
          unit={tDb("unit.sku")}
          delta={8.0}
          vs={tCommon("vsLastWeek")}
          spark={[2, 2, 3, 3, 4, 4, 5, 5, 6]}
        />
      </div>

      {/* 진행중 출고 */}
      <section className="mt-6 rounded-lg border border-border bg-panel p-5">
        <header className="mb-4 flex items-center justify-between">
          <h2 className="text-[13px] font-semibold text-text">
            {tDb("section.activeShipments")}
          </h2>
          <Link
            href="/shipments"
            className="text-[12px] text-text-3 hover:text-text-2"
          >
            {tDb("viewAll")}
          </Link>
        </header>
        {activeShipments.length === 0 ? (
          <div className="rounded-md border border-dashed border-border p-8 text-center text-[13px] text-text-3">
            {tDb("noActiveShipments")}
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {activeShipments.map((s) => {
              const cust = lookupCustomer(s.customerId);
              const wh = lookupWarehouse(s.warehouseId);
              const order = initialOrders.find((o) => o.id === s.orderId);
              return (
                <div
                  key={s.id}
                  className="rounded-md border border-border-2 bg-panel-2 p-3"
                >
                  <div className="mb-3 flex items-center justify-between">
                    <div className="flex items-center gap-2.5">
                      <span className="font-mono text-[11.5px] text-text-3">
                        SHP-{s.id}
                      </span>
                      <span className="text-[13px] font-medium text-text">
                        {cust.name}
                      </span>
                      <span className="text-[12px] text-text-3">
                        · {wh.name}
                      </span>
                      <span className="text-[12px] text-text-3">
                        · {fmtKRW(order?.total ?? 0)}
                      </span>
                    </div>
                    <StatusBadge
                      status={s.status}
                      live={s.status === "DISPATCHED"}
                    />
                  </div>
                  <Stepper steps={SHIPMENT_STEPS} current={s.status} />
                </div>
              );
            })}
          </div>
        )}
      </section>

      {/* 2단 그리드: 최근 주문 + 위험 SKU */}
      <section className="mt-6 grid grid-cols-1 gap-4 lg:grid-cols-2">
        {/* 최근 주문 */}
        <div className="rounded-lg border border-border bg-panel p-5">
          <header className="mb-3 flex items-center justify-between">
            <h2 className="text-[13px] font-semibold text-text">
              {tDb("section.recentOrders")}
            </h2>
            <Link
              href="/orders"
              className="text-[12px] text-text-3 hover:text-text-2"
            >
              {tDb("viewAll")}
            </Link>
          </header>
          <ul className="flex flex-col gap-2">
            {recentOrders.map((o) => {
              const cust = lookupCustomer(o.customerId);
              return (
                <li
                  key={o.id}
                  className="flex items-center justify-between border-b border-divider py-2 last:border-b-0"
                >
                  <div className="flex items-center gap-2.5 min-w-0">
                    <span className="font-mono text-[11.5px] text-text-3">
                      #{o.id}
                    </span>
                    <span className="truncate text-[13px] text-text">
                      {cust.name}
                    </span>
                  </div>
                  <div className="flex items-center gap-2.5">
                    <span className="font-medium tabular-nums text-text">
                      {fmtKRW(o.total)}
                    </span>
                    <StatusBadge status={o.status} />
                  </div>
                </li>
              );
            })}
          </ul>
        </div>

        {/* 위험 SKU */}
        <div className="rounded-lg border border-border bg-panel p-5">
          <header className="mb-3 flex items-center justify-between">
            <h2 className="text-[13px] font-semibold text-text">
              {tDb("section.criticalStocks")}
            </h2>
            <Link
              href="/stocks/inventory"
              className="text-[12px] text-text-3 hover:text-text-2"
            >
              {tDb("viewAll")}
            </Link>
          </header>
          {criticalStocks.length === 0 ? (
            <div className="py-6 text-center text-[13px] text-text-3">
              {tDb("noCriticalStocks")}
            </div>
          ) : (
            <ul className="flex flex-col gap-2">
              {criticalStocks.map((s) => {
                const p = lookupProduct(s.productId);
                const w = lookupWarehouse(s.warehouseId);
                return (
                  <li
                    key={`${s.productId}-${s.warehouseId}`}
                    className="flex items-center justify-between border-b border-divider py-2 last:border-b-0"
                  >
                    <div className="min-w-0">
                      <div className="truncate text-[13px] font-medium text-text">
                        {p.name}
                      </div>
                      <div className="text-[11px] text-text-3">
                        {p.sku} · {w.name}
                      </div>
                    </div>
                    <div className="flex items-center gap-2.5">
                      <span className="font-medium tabular-nums text-danger">
                        {fmtN(s.available)} {tDb("available")}
                      </span>
                      <Badge tone="danger">위험</Badge>
                    </div>
                  </li>
                );
              })}
            </ul>
          )}
        </div>
      </section>

      {/* 2단 그리드: 결재 대기 + 최근 알림 */}
      <section className="mt-6 grid grid-cols-1 gap-4 lg:grid-cols-2">
        {/* 결재 대기 */}
        <div className="rounded-lg border border-border bg-panel p-5">
          <header className="mb-3 flex items-center justify-between">
            <h2 className="text-[13px] font-semibold text-text">
              {tDb("section.openApprovals")}
            </h2>
            <Link
              href="/approvals"
              className="text-[12px] text-text-3 hover:text-text-2"
            >
              {tDb("viewAll")}
            </Link>
          </header>
          {openApprovals.length === 0 ? (
            <div className="py-6 text-center text-[13px] text-text-3">
              {tDb("noOpenApprovals")}
            </div>
          ) : (
            <ul className="flex flex-col gap-2">
              {openApprovals.map((a) => {
                const drafter = lookupEmployee(a.drafterId);
                return (
                  <li
                    key={a.id}
                    className="flex items-center justify-between border-b border-divider py-2 last:border-b-0"
                  >
                    <div className="min-w-0">
                      <div className="truncate text-[13px] font-medium text-text">
                        {a.title}
                      </div>
                      <div className="text-[11px] text-text-3">
                        AP-{a.id} · {drafter.name}
                      </div>
                    </div>
                    <div className="flex items-center gap-2.5">
                      {a.amount != null && (
                        <span className="text-[12px] tabular-nums text-text-2">
                          {fmtKRW(a.amount)}
                        </span>
                      )}
                      <span className="text-[11px] text-text-3 tabular-nums">
                        {tDb("step", {
                          current: a.currentStep,
                          total: a.totalSteps,
                        })}
                      </span>
                    </div>
                  </li>
                );
              })}
            </ul>
          )}
        </div>

        {/* 최근 알림 */}
        <div className="rounded-lg border border-border bg-panel p-5">
          <header className="mb-3 flex items-center justify-between">
            <h2 className="text-[13px] font-semibold text-text">
              {tDb("section.recentNotifications")}
            </h2>
            <Link
              href="/notifications"
              className="text-[12px] text-text-3 hover:text-text-2"
            >
              {tDb("viewAll")}
            </Link>
          </header>
          <ul className="flex flex-col gap-2">
            {recentNotifications.map((n) => {
              const recipient = lookupAppUser(n.recipientId);
              return (
                <li
                  key={n.id}
                  className="flex items-start justify-between gap-3 border-b border-divider py-2 last:border-b-0"
                >
                  <div className="min-w-0 flex-1">
                    <div className="truncate text-[13px] font-medium text-text">
                      {n.title}
                    </div>
                    <div className="truncate text-[11px] text-text-3">
                      → {recipient.username}
                      {n.sentAt && (
                        <span className="ml-1.5 tabular-nums">
                          · {fmtDateTime(n.sentAt)}
                        </span>
                      )}
                    </div>
                  </div>
                  <Badge
                    tone={
                      n.status === "SENT"
                        ? "ok"
                        : n.status === "PENDING"
                          ? "warn"
                          : "danger"
                    }
                  >
                    {n.channel}
                  </Badge>
                </li>
              );
            })}
          </ul>
        </div>
      </section>
    </div>
  );
}
