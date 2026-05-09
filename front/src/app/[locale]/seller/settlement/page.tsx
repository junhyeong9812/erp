import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { SellerSettlementView } from "@/features/seller/seller-settlement-view";
import { DEFAULT_SELLER_ID } from "@/lib/mock";

export default async function SellerSettlementPage() {
  const t = await getTranslations("Seller.settlement");

  return (
    <div className="px-6 py-6">
      <PageHead title={t("title")} sub={t("subtitle")} />
      <SellerSettlementView sellerId={DEFAULT_SELLER_ID} />
    </div>
  );
}
