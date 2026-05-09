"use client";

import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useErpStore } from "@/store/erp-store";
import { DeliveryDetailContent } from "./delivery-detail-content";

export function DeliveryDetailSheet() {
  const openDeliveryId = useErpStore((s) => s.openDeliveryId);
  const closeDelivery = useErpStore((s) => s.closeDelivery);

  return (
    <Sheet
      open={openDeliveryId !== null}
      onOpenChange={(o) => !o && closeDelivery()}
    >
      <SheetContent side="right" className="w-full sm:max-w-md">
        {openDeliveryId !== null && (
          <DeliveryDetailContent deliveryId={openDeliveryId} />
        )}
      </SheetContent>
    </Sheet>
  );
}
