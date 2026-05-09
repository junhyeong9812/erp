import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { PurchaseOrdersTable } from "@/features/purchase/purchase-orders-table";
import { PoCreateButton } from "@/features/purchase/po-create-button";
import { PoCreateSheet } from "@/features/purchase/po-create-sheet";
import { initialPurchaseOrders } from "@/lib/mock";

export default async function PurchasePage() {
  const tPage = await getTranslations("Pages.purchase");
  const tPurchase = await getTranslations("Purchase");

  const ongoing = initialPurchaseOrders.filter(
    (p) => p.status === "ISSUED" || p.status === "PARTIAL"
  ).length;

  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tPurchase("subtitleCount", {
          count: initialPurchaseOrders.length,
          ongoing,
        })}
        actions={<PoCreateButton />}
      />
      <PurchaseOrdersTable />
      <PoCreateSheet />
    </div>
  );
}
