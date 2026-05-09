"use client";

import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { useErpStore } from "@/store/erp-store";

export function OrderCreateButton() {
  const t = useTranslations("Orders");
  const open = useErpStore((s) => s.openOrderCreate);
  return (
    <Button size="sm" onClick={open}>
      <Icon.Plus className="size-3.5" />
      {t("newOrder")}
    </Button>
  );
}
