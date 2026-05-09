"use client";

import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { StatusBadge } from "@/components/erp/status-badge";
import { Stepper } from "@/components/erp/stepper";
import {
  SheetClose,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { fmtKRW, fmtN, fmtDateTime } from "@/lib/format";
import { lookupCustomer, lookupProduct } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import { ORDER_STEPS, SHIPMENT_STEPS } from "@/lib/types";

interface Props {
  orderId: number;
}

export function OrderDetailContent({ orderId }: Props) {
  const t = useTranslations("Orders.panel");
  const tCommon = useTranslations("Common");
  const tToast = useTranslations("Orders.toast");

  const { order, shipment, pay } = useErpStore(
    useShallow((s) => ({
      order: s.orders.find((o) => o.id === orderId),
      shipment: (() => {
        const o = s.orders.find((x) => x.id === orderId);
        if (!o?.shipmentId) return null;
        return s.shipments.find((sh) => sh.id === o.shipmentId) ?? null;
      })(),
      pay: s.payOrder,
    }))
  );

  if (!order) return null;

  const cust = lookupCustomer(order.customerId);

  const handlePay = () => {
    const result = pay(order.id);
    if (result.ok) {
      toast.success(tToast("paymentDone"));
    }
  };

  return (
    <>
      <SheetHeader>
        <SheetTitle>{t("title", { id: order.id })}</SheetTitle>
        <SheetDescription>
          {cust.code} · {cust.name}
        </SheetDescription>
      </SheetHeader>

      <div className="flex-1 overflow-y-auto px-5 py-4">
        <div className="mb-5">
          <Stepper steps={ORDER_STEPS} current={order.status} />
        </div>

        <section className="mb-5">
          <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
            {t("summary")}
          </h3>
          <dl className="grid grid-cols-[110px_1fr] gap-y-1.5 text-[13px]">
            <dt className="text-text-3">{t("kvCustomer")}</dt>
            <dd className="text-text">
              {cust.name}
              <span className="ml-1.5 text-text-3">({cust.grade})</span>
            </dd>
            <dt className="text-text-3">{t("kvPlacedAt")}</dt>
            <dd className="text-text tabular-nums">
              {fmtDateTime(order.placedAt)}
            </dd>
            <dt className="text-text-3">{t("kvTotal")}</dt>
            <dd className="font-medium text-text tabular-nums">
              {fmtKRW(order.total)}
            </dd>
            <dt className="text-text-3">{t("kvPayment")}</dt>
            <dd>
              <StatusBadge status={order.paid ? "COMPLETED" : "PENDING"} />
            </dd>
          </dl>
        </section>

        <section className="mb-5">
          <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
            {t("lineItems")}
          </h3>
          <table className="w-full text-[12.5px]">
            <thead>
              <tr className="border-b border-border text-left text-[11px] uppercase tracking-wider text-text-3">
                <th className="py-1.5">{t("colSku")}</th>
                <th className="py-1.5 text-right">{t("colQty")}</th>
                <th className="py-1.5 text-right">{t("colUnit")}</th>
                <th className="py-1.5 text-right">{t("colSubtotal")}</th>
              </tr>
            </thead>
            <tbody>
              {order.lines.map((l, i) => {
                const p = lookupProduct(l.productId);
                return (
                  <tr key={i} className="border-b border-divider">
                    <td className="py-2">
                      <div className="font-medium text-text">{p.name}</div>
                      <div className="font-mono text-[11px] text-text-3">
                        {p.sku}
                      </div>
                    </td>
                    <td className="py-2 text-right tabular-nums text-text-2">
                      {fmtN(l.quantity)}
                    </td>
                    <td className="py-2 text-right tabular-nums text-text-3">
                      {fmtKRW(l.unitPrice)}
                    </td>
                    <td className="py-2 text-right font-medium tabular-nums text-text">
                      {fmtKRW(l.quantity * l.unitPrice)}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </section>

        {shipment && (
          <section>
            <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
              {t("linkedShipment")}
            </h3>
            <div className="rounded-md border border-border bg-panel-2 p-3">
              <div className="mb-3 flex items-center justify-between">
                <span className="font-mono text-[11.5px] text-text-3">
                  SHP-{shipment.id}
                </span>
                <StatusBadge
                  status={shipment.status}
                  live={shipment.status === "DISPATCHED"}
                />
              </div>
              <Stepper steps={SHIPMENT_STEPS} current={shipment.status} />
            </div>
          </section>
        )}
      </div>

      <SheetFooter>
        <SheetClose asChild>
          <Button variant="outline" size="sm">
            {tCommon("close")}
          </Button>
        </SheetClose>
        {!order.paid && (
          <Button size="sm" onClick={handlePay}>
            {t("confirmPayment")}
          </Button>
        )}
      </SheetFooter>
    </>
  );
}
