import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { ReportsView } from "@/features/reports/reports-view";
import { REPORTS } from "@/lib/mock";

export default async function ReportsPage() {
  const tPage = await getTranslations("Pages.reports");
  const tReports = await getTranslations("Reports");
  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tReports("subtitleCount", { count: REPORTS.length })}
      />
      <ReportsView />
    </div>
  );
}
