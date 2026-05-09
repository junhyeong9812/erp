"use client";

import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useErpStore } from "@/store/erp-store";
import { OrderDetailContent } from "./order-detail-content";

export function OrderDetailSheet() {
  const openOrderId = useErpStore((s) => s.openOrderId);
  const closeOrder = useErpStore((s) => s.closeOrder);

  return (
    <Sheet
      open={openOrderId !== null}
      onOpenChange={(o) => !o && closeOrder()}
    >
      <SheetContent
        side="right"
        className="w-full sm:max-w-md"
      >
        {openOrderId !== null && <OrderDetailContent orderId={openOrderId} />}
      </SheetContent>
    </Sheet>
  );
}
