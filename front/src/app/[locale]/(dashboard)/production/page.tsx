import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { ProductionView } from "@/features/production/production-view";
import { WORK_ORDERS } from "@/lib/mock";

export default async function ProductionPage() {
  const tPage = await getTranslations("Pages.production");
  const tProduction = await getTranslations("Production");
  const inProgress = WORK_ORDERS.filter(
    (w) => w.status === "IN_PROGRESS"
  ).length;
  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tProduction("subtitleCount", {
          count: WORK_ORDERS.length,
          inProgress,
        })}
      />
      <ProductionView />
    </div>
  );
}
