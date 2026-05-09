import type { ReactNode } from "react";
import { SellerSidebar } from "@/components/layout/seller-sidebar";
import { SellerTopbar } from "@/components/layout/seller-topbar";

export default function SellerLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex h-screen overflow-hidden">
      <SellerSidebar />
      <div className="flex flex-1 flex-col overflow-hidden">
        <SellerTopbar />
        <main className="flex-1 overflow-y-auto bg-bg">{children}</main>
      </div>
    </div>
  );
}
