import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/routing";
import { Icon } from "@/components/icons";
import { LoginForm } from "@/features/admin/login-form";

export default async function LoginPage() {
  const tBrand = await getTranslations("Brand");
  const tLogin = await getTranslations("Login");

  return (
    <div className="flex min-h-screen items-center justify-center bg-bg px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex items-center justify-center gap-3">
          <div
            aria-hidden
            className="size-10 rounded-lg bg-accent shadow-[inset_0_-2px_0_rgba(0,0,0,0.08)]"
          />
          <div className="leading-tight">
            <div className="text-[18px] font-semibold tracking-tight text-text">
              {tBrand("name")}
            </div>
            <div className="text-[12px] text-text-3">{tBrand("subtitle")}</div>
          </div>
        </div>

        <div className="rounded-lg border border-border bg-panel p-6 shadow-sm">
          <h1 className="mb-1 text-[16px] font-semibold text-text">
            {tLogin("title")}
          </h1>
          <p className="mb-5 text-[12.5px] text-text-3">
            {tLogin("subtitle")}
          </p>
          <LoginForm />
        </div>

        <div className="mt-4 flex items-center justify-between text-[12px] text-text-3">
          <Link
            href="/dashboard"
            className="inline-flex items-center gap-1 hover:text-text-2"
          >
            <Icon.ChevronR className="size-3 rotate-180" />
            {tLogin("skipToDashboard")}
          </Link>
          <span>v0.1.0 · mock</span>
        </div>
      </div>
    </div>
  );
}
