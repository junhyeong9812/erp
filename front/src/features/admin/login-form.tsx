"use client";

import { useState, type FormEvent } from "react";
import { useTranslations } from "next-intl";
import { toast } from "sonner";
import { useRouter } from "@/i18n/routing";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export function LoginForm() {
  const t = useTranslations("Login");
  const router = useRouter();
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("•••••••");
  const [pending, setPending] = useState(false);

  const onSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!username.trim()) {
      toast.error(t("errorEmptyUsername"));
      return;
    }
    setPending(true);
    setTimeout(() => {
      toast.success(t("toastLoggedIn", { user: username }));
      router.push("/dashboard");
    }, 400);
  };

  return (
    <form onSubmit={onSubmit} className="flex flex-col gap-3">
      <div>
        <label
          htmlFor="username"
          className="mb-1 block text-[11.5px] font-medium text-text-2"
        >
          {t("usernameLabel")}
        </label>
        <Input
          id="username"
          autoComplete="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder={t("usernamePlaceholder")}
        />
      </div>
      <div>
        <label
          htmlFor="password"
          className="mb-1 block text-[11.5px] font-medium text-text-2"
        >
          {t("passwordLabel")}
        </label>
        <Input
          id="password"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder={t("passwordPlaceholder")}
        />
      </div>
      <Button type="submit" size="lg" disabled={pending} className="mt-2">
        {pending ? t("signingIn") : t("signIn")}
      </Button>
      <div className="rounded-md bg-bg-elev p-2.5 text-[11px] text-text-3">
        {t("mockHint")}
      </div>
    </form>
  );
}
