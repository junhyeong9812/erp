"use client";

import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useErpStore } from "@/store/erp-store";
import { ShipmentDetailContent } from "./shipment-detail-content";

export function ShipmentDetailSheet() {
  const openShipmentId = useErpStore((s) => s.openShipmentId);
  const closeShipment = useErpStore((s) => s.closeShipment);

  return (
    <Sheet
      open={openShipmentId !== null}
      onOpenChange={(o) => !o && closeShipment()}
    >
      <SheetContent side="right" className="w-full sm:max-w-md">
        {openShipmentId !== null && (
          <ShipmentDetailContent shipmentId={openShipmentId} />
        )}
      </SheetContent>
    </Sheet>
  );
}
