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
import { fmtDateTime, fmtKRW } from "@/lib/format";
import { lookupCustomer, lookupWarehouse } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import { SHIPMENT_STEPS, DELIVERY_STEPS } from "@/lib/types";

interface Props {
  shipmentId: number;
}

export function ShipmentDetailContent({ shipmentId }: Props) {
  const t = useTranslations("Shipments.panel");
  const tCommon = useTranslations("Common");
  const tToast = useTranslations("Shipments.toast");

  const { shipment, order, delivery, dispatch } = useErpStore(
    useShallow((s) => ({
      shipment: s.shipments.find((x) => x.id === shipmentId),
      order: (() => {
        const sh = s.shipments.find((x) => x.id === shipmentId);
        if (!sh) return null;
        return s.orders.find((o) => o.id === sh.orderId) ?? null;
      })(),
      delivery: s.deliveries.find((d) => d.shipmentId === shipmentId) ?? null,
      dispatch: s.dispatchShipment,
    }))
  );

  if (!shipment) return null;

  const cust = lookupCustomer(shipment.customerId);
  const wh = lookupWarehouse(shipment.warehouseId);

  const handleDispatch = () => {
    const result = dispatch(shipment.id);
    if (result.ok) {
      toast.success(tToast("dispatched"));
    }
  };

  return (
    <>
      <SheetHeader>
        <SheetTitle>{t("title", { id: shipment.id })}</SheetTitle>
        <SheetDescription>
          {cust.code} · {cust.name} · {wh.name}
        </SheetDescription>
      </SheetHeader>

      <div className="flex-1 overflow-y-auto px-5 py-4">
        <div className="mb-5">
          <Stepper steps={SHIPMENT_STEPS} current={shipment.status} />
        </div>

        <section className="mb-5">
          <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
            {t("summary")}
          </h3>
          <dl className="grid grid-cols-[120px_1fr] gap-y-1.5 text-[13px]">
            <dt className="text-text-3">{t("kvWarehouse")}</dt>
            <dd className="text-text">{wh.name}</dd>
            <dt className="text-text-3">{t("kvItems")}</dt>
            <dd className="tabular-nums text-text">
              {shipment.items}
            </dd>
            <dt className="text-text-3">{t("kvWeight")}</dt>
            <dd className="tabular-nums text-text">
              {shipment.weightKg}kg
            </dd>
            <dt className="text-text-3">{t("kvDriver")}</dt>
            <dd className={shipment.driver ? "text-text" : "text-text-3"}>
              {shipment.driver ?? t("noDriver")}
            </dd>
            <dt className="text-text-3">{t("kvTracking")}</dt>
            <dd className="font-mono text-[12px] text-text">
              {shipment.tracking ?? "—"}
            </dd>
            <dt className="text-text-3">{t("kvPreparedAt")}</dt>
            <dd className="tabular-nums text-text-2">
              {fmtDateTime(shipment.preparedAt)}
            </dd>
            <dt className="text-text-3">{t("kvDispatchedAt")}</dt>
            <dd className="tabular-nums text-text-2">
              {fmtDateTime(shipment.dispatchedAt)}
            </dd>
            <dt className="text-text-3">{t("kvDeliveredAt")}</dt>
            <dd className="tabular-nums text-text-2">
              {fmtDateTime(shipment.deliveredAt)}
            </dd>
          </dl>
        </section>

        {order && (
          <section className="mb-5">
            <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
              {t("linkedOrder")}
            </h3>
            <div className="rounded-md border border-border bg-panel-2 p-3">
              <div className="flex items-center justify-between">
                <span className="font-mono text-[11.5px] text-text-3">
                  #{order.id}
                </span>
                <StatusBadge status={order.status} />
              </div>
              <div className="mt-1.5 text-[12.5px] tabular-nums text-text-2">
                {fmtKRW(order.total)}
              </div>
            </div>
          </section>
        )}

        {delivery && (
          <section>
            <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
              {t("linkedDelivery")}
            </h3>
            <div className="rounded-md border border-border bg-panel-2 p-3">
              <div className="mb-3 flex items-center justify-between">
                <span className="font-mono text-[11.5px] text-text-3">
                  DLV-{delivery.id}
                </span>
                <StatusBadge
                  status={delivery.status}
                  live={delivery.status === "IN_TRANSIT"}
                />
              </div>
              <Stepper steps={DELIVERY_STEPS} current={delivery.status} />
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
        {shipment.status === "PREPARING" && (
          <Button size="sm" onClick={handleDispatch}>
            {t("confirmDispatch")}
          </Button>
        )}
      </SheetFooter>
    </>
  );
}
