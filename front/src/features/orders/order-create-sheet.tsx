"use client";

import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useErpStore } from "@/store/erp-store";
import { OrderCreateForm } from "./order-create-form";

export function OrderCreateSheet() {
  const open = useErpStore((s) => s.orderCreateOpen);
  const close = useErpStore((s) => s.closeOrderCreate);

  return (
    <Sheet open={open} onOpenChange={(o) => !o && close()}>
      <SheetContent
        side="right"
        className="flex w-full flex-col p-0 sm:max-w-lg"
      >
        {open && <OrderCreateForm />}
      </SheetContent>
    </Sheet>
  );
}
