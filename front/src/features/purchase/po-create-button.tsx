"use client";

import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { useErpStore } from "@/store/erp-store";

export function PoCreateButton() {
  const t = useTranslations("Purchase");
  const open = useErpStore((s) => s.openPurchaseOrderCreate);
  return (
    <Button size="sm" onClick={open}>
      <Icon.Plus className="size-3.5" />
      {t("newPo")}
    </Button>
  );
}
