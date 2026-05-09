"use client";

import { useTranslations } from "next-intl";
import { useForm, useFieldArray, Controller } from "react-hook-form";
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
import { Icon } from "@/components/icons";
import { fmtKRW } from "@/lib/format";
import { CUSTOMERS, PRODUCTS } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";

const orderLineSchema = z.object({
  productId: z.coerce.number().int().min(1),
  quantity: z.coerce.number().int().min(1),
  unitPrice: z.coerce.number().int().min(1),
});

const orderCreateSchema = z.object({
  customerId: z.coerce.number().int().min(1),
  lines: z.array(orderLineSchema).min(1),
});

type FormValues = z.infer<typeof orderCreateSchema>;

const DEFAULT_VALUES: FormValues = {
  customerId: 0,
  lines: [{ productId: 0, quantity: 1, unitPrice: 0 }],
};

export function OrderCreateForm() {
  const t = useTranslations("Orders.create");
  const tCommon = useTranslations("Common");

  const create = useErpStore((s) => s.createOrder);
  const close = useErpStore((s) => s.closeOrderCreate);
  const openOrder = useErpStore((s) => s.openOrder);

  const {
    register,
    control,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<FormValues>({
    resolver: zodResolver(orderCreateSchema),
    defaultValues: DEFAULT_VALUES,
  });

  const { fields, append, remove } = useFieldArray({ control, name: "lines" });

  const lines = watch("lines");
  const total = lines.reduce(
    (sum, l) => sum + Number(l.quantity || 0) * Number(l.unitPrice || 0),
    0
  );

  const onSubmit = (data: FormValues) => {
    const result = create({
      customerId: data.customerId,
      lines: data.lines.map((l) => ({
        productId: l.productId,
        quantity: l.quantity,
        unitPrice: l.unitPrice,
      })),
    });
    if (result.ok && result.value != null) {
      const newId = result.value;
      toast.success(t("toastCreated", { id: newId }));
      reset(DEFAULT_VALUES);
      close();
      openOrder(newId);
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
          {/* 고객 선택 */}
          <div className="mb-4">
            <label
              htmlFor="customerId"
              className="mb-1 block text-[11.5px] font-medium text-text-2"
            >
              {t("customerLabel")}
            </label>
            <Select
              id="customerId"
              invalid={!!errors.customerId}
              {...register("customerId")}
            >
              <option value={0}>{t("customerPlaceholder")}</option>
              {CUSTOMERS.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} ({c.code} · {c.grade})
                </option>
              ))}
            </Select>
            {errors.customerId && (
              <p className="mt-1 text-[11px] text-danger">
                {t("errorCustomer")}
              </p>
            )}
          </div>

          {/* 라인 아이템 */}
          <div className="mb-3 flex items-center justify-between">
            <h3 className="text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
              {t("linesLabel")}
            </h3>
            <Button
              type="button"
              variant="ghost"
              size="sm"
              onClick={() =>
                append({ productId: 0, quantity: 1, unitPrice: 0 })
              }
            >
              <Icon.Plus className="size-3.5" />
              {t("addLine")}
            </Button>
          </div>

          <div className="flex flex-col gap-2">
            {fields.map((field, index) => {
              const lineErrors = errors.lines?.[index];
              const productId = lines[index]?.productId ?? 0;
              const quantity = Number(lines[index]?.quantity ?? 0);
              const unitPrice = Number(lines[index]?.unitPrice ?? 0);
              return (
                <div
                  key={field.id}
                  className="rounded-md border border-border bg-panel-2 p-3"
                >
                  <div className="grid grid-cols-[1fr_80px_120px_28px] gap-2">
                    <Controller
                      control={control}
                      name={`lines.${index}.productId`}
                      render={({ field: f }) => (
                        <Select
                          value={String(f.value)}
                          invalid={!!lineErrors?.productId}
                          onChange={(e) => {
                            const pid = Number(e.target.value);
                            f.onChange(pid);
                            const p = PRODUCTS.find((x) => x.id === pid);
                            if (p) {
                              setValue(`lines.${index}.unitPrice`, p.price);
                            }
                          }}
                        >
                          <option value={0}>
                            {t("productPlaceholder")}
                          </option>
                          {PRODUCTS.map((p) => (
                            <option key={p.id} value={p.id}>
                              {p.name} ({p.sku})
                            </option>
                          ))}
                        </Select>
                      )}
                    />
                    <Input
                      type="number"
                      min={1}
                      placeholder={t("qtyPlaceholder")}
                      aria-invalid={!!lineErrors?.quantity || undefined}
                      className={
                        lineErrors?.quantity ? "border-danger" : undefined
                      }
                      {...register(`lines.${index}.quantity`)}
                    />
                    <Input
                      type="number"
                      min={1}
                      placeholder={t("priceePlaceholder")}
                      aria-invalid={!!lineErrors?.unitPrice || undefined}
                      className={
                        lineErrors?.unitPrice ? "border-danger" : undefined
                      }
                      {...register(`lines.${index}.unitPrice`)}
                    />
                    <button
                      type="button"
                      onClick={() => remove(index)}
                      disabled={fields.length === 1}
                      className="inline-flex size-7 items-center justify-center rounded-md text-text-3 transition-colors hover:bg-hover hover:text-danger disabled:cursor-not-allowed disabled:opacity-30"
                      aria-label={t("removeLine")}
                    >
                      <Icon.X className="size-3.5" />
                    </button>
                  </div>
                  <div className="mt-2 flex items-center justify-between text-[11px]">
                    <span className="text-text-3">
                      {t("subtotalLabel")}
                    </span>
                    <span className="font-medium tabular-nums text-text">
                      {fmtKRW(quantity * unitPrice)}
                    </span>
                  </div>
                  {(lineErrors?.productId ||
                    lineErrors?.quantity ||
                    lineErrors?.unitPrice) && (
                    <p className="mt-1.5 text-[11px] text-danger">
                      {t("errorLine")}
                    </p>
                  )}
                </div>
              );
            })}
          </div>

          {errors.lines &&
            !Array.isArray(errors.lines) &&
            errors.lines.message && (
              <p className="mt-2 text-[11.5px] text-danger">
                {t("errorAtLeastOne")}
              </p>
            )}

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
