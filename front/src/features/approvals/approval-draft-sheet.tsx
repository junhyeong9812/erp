"use client";

import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useErpStore } from "@/store/erp-store";
import { ApprovalDraftForm } from "./approval-draft-form";

export function ApprovalDraftSheet() {
  const open = useErpStore((s) => s.approvalDraftOpen);
  const close = useErpStore((s) => s.closeApprovalDraft);

  return (
    <Sheet open={open} onOpenChange={(o) => !o && close()}>
      <SheetContent
        side="right"
        className="flex w-full flex-col p-0 sm:max-w-lg"
      >
        {open && <ApprovalDraftForm />}
      </SheetContent>
    </Sheet>
  );
}
