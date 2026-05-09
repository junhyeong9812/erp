import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { SellerOrdersView } from "@/features/seller/seller-orders-view";
import { DEFAULT_SELLER_ID, initialOrders } from "@/lib/mock";
import { sellerOrders } from "@/features/seller/helpers";

export default async function SellerOrdersPage() {
  const t = await getTranslations("Seller.orders");
  const count = sellerOrders(DEFAULT_SELLER_ID, [...initialOrders]).length;

  return (
    <div className="px-6 py-6">
      <PageHead title={t("title")} sub={t("subtitle", { count })} />
      <SellerOrdersView sellerId={DEFAULT_SELLER_ID} />
    </div>
  );
}
