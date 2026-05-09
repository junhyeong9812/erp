import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { SellerProductsView } from "@/features/seller/seller-products-view";
import {
  DEFAULT_SELLER_ID,
  initialStocks,
} from "@/lib/mock";
import { sellerProducts, sellerStocks } from "@/features/seller/helpers";
import { severityOf } from "@/features/stocks/severity";

export default async function SellerProductsPage() {
  const t = await getTranslations("Seller.products");

  const myProducts = sellerProducts(DEFAULT_SELLER_ID);
  const myStocks = sellerStocks(DEFAULT_SELLER_ID, [...initialStocks]);
  const critical = myProducts.filter((p) => {
    const totalAvail = myStocks
      .filter((s) => s.productId === p.id)
      .reduce((sum, s) => sum + (s.total - s.reserved), 0);
    return severityOf(totalAvail) === "critical";
  }).length;

  return (
    <div className="px-6 py-6">
      <PageHead
        title={t("title")}
        sub={t("subtitle", { count: myProducts.length, critical })}
      />
      <SellerProductsView sellerId={DEFAULT_SELLER_ID} />
    </div>
  );
}
