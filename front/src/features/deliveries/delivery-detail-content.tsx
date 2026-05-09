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
import { fmtDateTime } from "@/lib/format";
import { lookupCustomer } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import { DELIVERY_STEPS, SHIPMENT_STEPS } from "@/lib/types";

interface Props {
  deliveryId: number;
}

export function DeliveryDetailContent({ deliveryId }: Props) {
  const t = useTranslations("Deliveries.panel");
  const tCommon = useTranslations("Common");
  const tToast = useTranslations("Deliveries.toast");

  const { delivery, shipment, complete } = useErpStore(
    useShallow((s) => ({
      delivery: s.deliveries.find((d) => d.id === deliveryId),
      shipment: (() => {
        const d = s.deliveries.find((x) => x.id === deliveryId);
        if (!d) return null;
        return s.shipments.find((sh) => sh.id === d.shipmentId) ?? null;
      })(),
      complete: s.completeDelivery,
    }))
  );

  if (!delivery) return null;

  const cust = lookupCustomer(delivery.customerId);

  const handleComplete = () => {
    const result = complete(delivery.id);
    if (result.ok) {
      toast.success(tToast("completed"));
    }
  };

  const inFlight =
    delivery.status === "ASSIGNED" || delivery.status === "IN_TRANSIT";

  return (
    <>
      <SheetHeader>
        <SheetTitle>{t("title", { id: delivery.id })}</SheetTitle>
        <SheetDescription>
          {cust.code} · {cust.name} · {delivery.region}
        </SheetDescription>
      </SheetHeader>

      <div className="flex-1 overflow-y-auto px-5 py-4">
        <div className="mb-5">
          <Stepper steps={DELIVERY_STEPS} current={delivery.status} />
        </div>

        <section className="mb-5">
          <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
            {t("summary")}
          </h3>
          <dl className="grid grid-cols-[120px_1fr] gap-y-1.5 text-[13px]">
            <dt className="text-text-3">{t("kvCourier")}</dt>
            <dd className="text-text">{delivery.courier}</dd>
            <dt className="text-text-3">{t("kvDriver")}</dt>
            <dd className="text-text">{delivery.driver}</dd>
            <dt className="text-text-3">{t("kvRegion")}</dt>
            <dd className="text-text">{delivery.region}</dd>
            <dt className="text-text-3">{t("kvEta")}</dt>
            <dd className="tabular-nums text-text-2">
              {fmtDateTime(delivery.eta)}
            </dd>
            <dt className="text-text-3">{t("kvDeliveredAt")}</dt>
            <dd className="tabular-nums text-text-2">
              {fmtDateTime(delivery.deliveredAt)}
            </dd>
            <dt className="text-text-3">{t("kvShipment")}</dt>
            <dd className="font-mono text-[12px] text-text-2">
              SHP-{delivery.shipmentId}
            </dd>
          </dl>
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
        {inFlight && (
          <Button size="sm" onClick={handleComplete}>
            {t("confirmComplete")}
          </Button>
        )}
      </SheetFooter>
    </>
  );
}
