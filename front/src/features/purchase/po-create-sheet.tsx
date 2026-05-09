"use client";

import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useErpStore } from "@/store/erp-store";
import { PoCreateForm } from "./po-create-form";

export function PoCreateSheet() {
  const open = useErpStore((s) => s.purchaseOrderCreateOpen);
  const close = useErpStore((s) => s.closePurchaseOrderCreate);

  return (
    <Sheet open={open} onOpenChange={(o) => !o && close()}>
      <SheetContent
        side="right"
        className="flex w-full flex-col p-0 sm:max-w-lg"
      >
        {open && <PoCreateForm />}
      </SheetContent>
    </Sheet>
  );
}
