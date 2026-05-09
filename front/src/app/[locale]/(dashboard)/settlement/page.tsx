import { redirect } from "@/i18n/routing";

export default async function SettlementIndex({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  redirect({ href: "/settlement/periods", locale });
}
