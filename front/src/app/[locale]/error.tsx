"use client";

import { useEffect } from "react";
import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";

interface Props {
  error: Error & { digest?: string };
  reset: () => void;
}

export default function ErrorBoundary({ error, reset }: Props) {
  const t = useTranslations("System.error");

  useEffect(() => {
    console.error("[App Error Boundary]", error);
  }, [error]);

  return (
    <div className="flex min-h-screen items-center justify-center bg-bg px-4">
      <div className="w-full max-w-md text-center">
        <div className="mb-6 inline-flex size-14 items-center justify-center rounded-full bg-danger-soft text-danger-ink">
          <Icon.Warn className="size-6" />
        </div>
        <h1 className="text-[20px] font-semibold tracking-tight text-text">
          {t("title")}
        </h1>
        <p className="mt-1 text-[13px] text-text-3">{t("subtitle")}</p>
        {error.digest && (
          <p className="mt-3 font-mono text-[11px] text-text-3">
            {t("digest")}: {error.digest}
          </p>
        )}
        <div className="mt-6 flex items-center justify-center gap-2">
          <Button onClick={reset}>
            <Icon.Refresh className="size-3.5" />
            {t("retry")}
          </Button>
          <Button
            variant="outline"
            onClick={() => {
              window.location.href = "/dashboard";
            }}
          >
            {t("goHome")}
          </Button>
        </div>
      </div>
    </div>
  );
}
