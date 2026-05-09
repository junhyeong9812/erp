"use client";

import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useErpStore } from "@/store/erp-store";
import { PurchaseOrderDetailContent } from "./purchase-order-detail-content";

export function PurchaseOrderDetailSheet() {
  const openId = useErpStore((s) => s.openPurchaseOrderId);
  const close = useErpStore((s) => s.closePurchaseOrder);

  return (
    <Sheet open={openId !== null} onOpenChange={(o) => !o && close()}>
      <SheetContent side="right" className="w-full sm:max-w-md">
        {openId !== null && <PurchaseOrderDetailContent poId={openId} />}
      </SheetContent>
    </Sheet>
  );
}
