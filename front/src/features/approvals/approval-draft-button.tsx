"use client";

import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { useErpStore } from "@/store/erp-store";

export function ApprovalDraftButton() {
  const t = useTranslations("Approvals.create");
  const open = useErpStore((s) => s.openApprovalDraft);
  return (
    <Button size="sm" onClick={open}>
      <Icon.Plus className="size-3.5" />
      {t("triggerLabel")}
    </Button>
  );
}
