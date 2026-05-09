import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { ShipmentsTable } from "@/features/shipments/shipments-table";
import { initialShipments } from "@/lib/mock";

export default async function ShipmentsPage() {
  const tPage = await getTranslations("Pages.shipments");
  const tShipments = await getTranslations("Shipments");

  const active = initialShipments.filter(
    (s) => s.status === "PREPARING" || s.status === "DISPATCHED"
  ).length;

  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tShipments("subtitleCount", {
          count: initialShipments.length,
          active,
        })}
      />
      <ShipmentsTable />
    </div>
  );
}
