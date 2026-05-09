import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { SuppliersView } from "@/features/suppliers/suppliers-view";
import { SUPPLIERS } from "@/lib/mock";

export default async function SuppliersPage() {
  const tPage = await getTranslations("Pages.suppliers");
  const tSuppliers = await getTranslations("Suppliers");
  const active = SUPPLIERS.filter((s) => s.status === "ACTIVE").length;
  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tSuppliers("subtitleCount", {
          count: SUPPLIERS.length,
          active,
        })}
      />
      <SuppliersView />
    </div>
  );
}
