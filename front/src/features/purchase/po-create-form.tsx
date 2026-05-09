"use client";

import { useTranslations } from "next-intl";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import {
  SheetClose,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { fmtKRW } from "@/lib/format";
import { PRODUCTS, SUPPLIERS } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";

const poCreateSchema = z.object({
  supplier: z.string().min(1),
  productId: z.coerce.number().int().min(1),
  quantity: z.coerce.number().int().min(1),
  unitPrice: z.coerce.number().int().min(1),
});

type FormValues = z.infer<typeof poCreateSchema>;

const DEFAULT_VALUES: FormValues = {
  supplier: "",
  productId: 0,
  quantity: 1,
  unitPrice: 0,
};

export function PoCreateForm() {
  const t = useTranslations("Purchase.create");
  const tCommon = useTranslations("Common");

  const create = useErpStore((s) => s.createPurchaseOrder);
  const close = useErpStore((s) => s.closePurchaseOrderCreate);
  const openPo = useErpStore((s) => s.openPurchaseOrder);

  const {
    register,
    control,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<FormValues>({
    resolver: zodResolver(poCreateSchema),
    defaultValues: DEFAULT_VALUES,
  });

  const quantity = Number(watch("quantity") || 0);
  const unitPrice = Number(watch("unitPrice") || 0);
  const total = quantity * unitPrice;

  const onSubmit = (data: FormValues) => {
    const result = create({
      supplier: data.supplier,
      productId: data.productId,
      quantity: data.quantity,
      unitPrice: data.unitPrice,
    });
    if (result.ok && result.value != null) {
      const newId = result.value;
      toast.success(t("toastCreated", { id: newId }));
      reset(DEFAULT_VALUES);
      close();
      openPo(newId);
    } else if (!result.ok) {
      toast.error(t("toastFailed"));
    }
  };

  return (
    <>
      <SheetHeader>
        <SheetTitle>{t("title")}</SheetTitle>
        <SheetDescription>{t("subtitle")}</SheetDescription>
      </SheetHeader>

      <form
        onSubmit={handleSubmit(onSubmit)}
        className="flex flex-1 flex-col overflow-hidden"
      >
        <div className="flex-1 overflow-y-auto px-5 py-4">
          {/* 공급자 */}
          <div className="mb-4">
            <label
              htmlFor="supplier"
              className="mb-1 block text-[11.5px] font-medium text-text-2"
            >
              {t("supplierLabel")}
            </label>
            <Select
              id="supplier"
              invalid={!!errors.supplier}
              {...register("supplier")}
            >
              <option value="">{t("supplierPlaceholder")}</option>
              {SUPPLIERS.filter((s) => s.status === "ACTIVE").map((s) => (
                <option key={s.id} value={s.name}>
                  {s.name} ({s.code} · {s.category})
                </option>
              ))}
            </Select>
            {errors.supplier && (
              <p className="mt-1 text-[11px] text-danger">
                {t("errorSupplier")}
              </p>
            )}
          </div>

          {/* 상품 */}
          <div className="mb-4">
            <label
              htmlFor="productId"
              className="mb-1 block text-[11.5px] font-medium text-text-2"
            >
              {t("productLabel")}
            </label>
            <Controller
              control={control}
              name="productId"
              render={({ field }) => (
                <Select
                  id="productId"
                  value={String(field.value)}
                  invalid={!!errors.productId}
                  onChange={(e) => {
                    const pid = Number(e.target.value);
                    field.onChange(pid);
                    const p = PRODUCTS.find((x) => x.id === pid);
                    if (p) setValue("unitPrice", p.price);
                  }}
                >
                  <option value={0}>{t("productPlaceholder")}</option>
                  {PRODUCTS.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name} ({p.sku})
                    </option>
                  ))}
                </Select>
              )}
            />
            {errors.productId && (
              <p className="mt-1 text-[11px] text-danger">
                {t("errorProduct")}
              </p>
            )}
          </div>

          {/* 수량 + 단가 */}
          <div className="mb-4 grid grid-cols-2 gap-3">
            <div>
              <label
                htmlFor="quantity"
                className="mb-1 block text-[11.5px] font-medium text-text-2"
              >
                {t("qtyLabel")}
              </label>
              <Input
                id="quantity"
                type="number"
                min={1}
                placeholder={t("qtyPlaceholder")}
                aria-invalid={!!errors.quantity || undefined}
                className={errors.quantity ? "border-danger" : undefined}
                {...register("quantity")}
              />
              {errors.quantity && (
                <p className="mt-1 text-[11px] text-danger">
                  {t("errorQty")}
                </p>
              )}
            </div>
            <div>
              <label
                htmlFor="unitPrice"
                className="mb-1 block text-[11.5px] font-medium text-text-2"
              >
                {t("priceLabel")}
              </label>
              <Input
                id="unitPrice"
                type="number"
                min={1}
                placeholder={t("pricePlaceholder")}
                aria-invalid={!!errors.unitPrice || undefined}
                className={errors.unitPrice ? "border-danger" : undefined}
                {...register("unitPrice")}
              />
              {errors.unitPrice && (
                <p className="mt-1 text-[11px] text-danger">
                  {t("errorPrice")}
                </p>
              )}
            </div>
          </div>

          {/* 총액 */}
          <div className="mt-4 flex items-center justify-between rounded-md border border-accent-soft bg-accent-soft/30 px-4 py-3">
            <span className="text-[12px] font-medium text-accent-ink">
              {t("totalLabel")}
            </span>
            <span className="text-[16px] font-semibold tabular-nums text-accent-ink">
              {fmtKRW(total)}
            </span>
          </div>
        </div>

        <SheetFooter>
          <SheetClose asChild>
            <Button type="button" variant="outline" size="sm">
              {tCommon("cancel")}
            </Button>
          </SheetClose>
          <Button type="submit" size="sm" disabled={isSubmitting}>
            {isSubmitting ? tCommon("loading") : t("submit")}
          </Button>
        </SheetFooter>
      </form>
    </>
  );
}
