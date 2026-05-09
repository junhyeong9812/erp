"use client";

import { useTranslations } from "next-intl";
import { Badge } from "@/components/ui/badge";
import { STATUS_TONE, type StatusKey } from "@/lib/types";

interface Props {
  status: StatusKey;
  live?: boolean;
}

export function StatusBadge({ status, live }: Props) {
  const t = useTranslations("Status");
  const tone = STATUS_TONE[status] ?? "neutral";
  return (
    <Badge tone={tone} live={live}>
      {t(status)}
    </Badge>
  );
}
