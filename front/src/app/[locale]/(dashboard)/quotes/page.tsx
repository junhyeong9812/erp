import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { QuotesView } from "@/features/quotes/quotes-view";
import { QUOTES } from "@/lib/mock";

export default async function QuotesPage() {
  const tPage = await getTranslations("Pages.quotes");
  const tQuotes = await getTranslations("Quotes");
  const active = QUOTES.filter((q) => q.status === "ACTIVE").length;
  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tQuotes("subtitleCount", { count: QUOTES.length, active })}
      />
      <QuotesView />
    </div>
  );
}
