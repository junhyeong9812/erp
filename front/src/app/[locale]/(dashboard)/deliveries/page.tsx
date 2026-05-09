import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { DeliveriesTable } from "@/features/deliveries/deliveries-table";
import { initialDeliveries } from "@/lib/mock";

export default async function DeliveriesPage() {
  const tPage = await getTranslations("Pages.deliveries");
  const tDeliveries = await getTranslations("Deliveries");

  const inTransit = initialDeliveries.filter(
    (d) => d.status === "IN_TRANSIT" || d.status === "ASSIGNED"
  ).length;

  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tDeliveries("subtitleCount", {
          count: initialDeliveries.length,
          inTransit,
        })}
      />
      <DeliveriesTable />
    </div>
  );
}
