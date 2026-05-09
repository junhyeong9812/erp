import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { SellerDashboardView } from "@/features/seller/seller-dashboard-view";
import { DEFAULT_SELLER_ID, lookupSeller } from "@/lib/mock";

export default async function SellerDashboardPage() {
  const t = await getTranslations("Seller.dashboard");
  const seller = lookupSeller(DEFAULT_SELLER_ID);

  return (
    <div className="px-6 py-6">
      <PageHead
        title={t("title")}
        sub={t("subtitle", { name: seller.name })}
      />
      <SellerDashboardView sellerId={DEFAULT_SELLER_ID} />
    </div>
  );
}
