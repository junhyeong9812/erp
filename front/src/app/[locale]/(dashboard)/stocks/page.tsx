import { redirect } from "@/i18n/routing";

export default async function StocksIndex({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  redirect({ href: "/stocks/inventory", locale });
}
