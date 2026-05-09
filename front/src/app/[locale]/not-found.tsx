import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/routing";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";

export default async function NotFound() {
  const t = await getTranslations("System.notFound");

  return (
    <div className="flex min-h-screen items-center justify-center bg-bg px-4">
      <div className="w-full max-w-md text-center">
        <div className="mb-6 inline-flex size-14 items-center justify-center rounded-full bg-warn-soft text-warn-ink">
          <Icon.Warn className="size-6" />
        </div>
        <h1 className="text-[28px] font-semibold tracking-tight text-text">
          404
        </h1>
        <p className="mt-2 text-[15px] font-medium text-text">
          {t("title")}
        </p>
        <p className="mt-1 text-[13px] text-text-3">{t("subtitle")}</p>
        <Link
          href="/dashboard"
          className="mt-6 inline-block"
        >
          <Button>
            <Icon.ChevronR className="size-3.5 rotate-180" />
            {t("goHome")}
          </Button>
        </Link>
      </div>
    </div>
  );
}
