import type { SVGProps } from "react";

const ICO_PROPS: SVGProps<SVGSVGElement> = {
  width: 16,
  height: 16,
  viewBox: "0 0 16 16",
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.5,
  strokeLinecap: "round",
  strokeLinejoin: "round",
};

type IconComponent = (props?: SVGProps<SVGSVGElement>) => React.JSX.Element;

const make = (children: React.ReactNode): IconComponent =>
  function IconImpl(props) {
    return (
      <svg {...ICO_PROPS} {...props}>
        {children}
      </svg>
    );
  };

export const Icon = {
  Dashboard: make(
    <>
      <rect x="2" y="2" width="5" height="5" rx="1" />
      <rect x="9" y="2" width="5" height="5" rx="1" />
      <rect x="2" y="9" width="5" height="5" rx="1" />
      <rect x="9" y="9" width="5" height="5" rx="1" />
    </>
  ),
  Cart: make(
    <>
      <path d="M2 3h2l2 8h8l1.5-6H5" />
      <circle cx="6.5" cy="13.5" r="0.8" />
      <circle cx="12.5" cy="13.5" r="0.8" />
    </>
  ),
  Truck: make(
    <>
      <path d="M1.5 4h8v8h-8z" />
      <path d="M9.5 7h3l2 2v3h-5" />
      <circle cx="4" cy="13" r="1.2" />
      <circle cx="12" cy="13" r="1.2" />
    </>
  ),
  Route: make(
    <>
      <circle cx="3" cy="3" r="1.5" />
      <circle cx="13" cy="13" r="1.5" />
      <path d="M3 4.5v3a3 3 0 0 0 3 3h4a3 3 0 0 1 3 3" />
    </>
  ),
  Box: make(
    <>
      <path d="M2 5l6-3 6 3v6l-6 3-6-3z" />
      <path d="M2 5l6 3 6-3M8 8v6" />
    </>
  ),
  Doc: make(
    <>
      <path d="M3 2h7l3 3v9H3z" />
      <path d="M10 2v3h3" />
      <path d="M5 8h6M5 11h4" />
    </>
  ),
  Search: make(
    <>
      <circle cx="7" cy="7" r="4.5" />
      <path d="m10.5 10.5 3 3" />
    </>
  ),
  Bell: make(
    <>
      <path d="M4 6a4 4 0 0 1 8 0v3l1.5 2H2.5L4 9z" />
      <path d="M6.5 13a1.5 1.5 0 0 0 3 0" />
    </>
  ),
  Plus: make(<path d="M8 3v10M3 8h10" />),
  Filter: make(<path d="M2 3h12l-4.5 6v4l-3 1V9z" />),
  Download: make(<path d="M8 2v8m-3-3 3 3 3-3M3 12h10" />),
  ChevronR: make(<path d="m6 3 5 5-5 5" />),
  ChevronD: make(<path d="m3 6 5 5 5-5" />),
  ArrowUp: make(<path d="m4 9 4-4 4 4M8 5v9" />),
  ArrowDown: make(<path d="m4 7 4 4 4-4M8 11V2" />),
  Check: make(<path d="m3 8 3 3 7-7" />),
  X: make(<path d="M3 3l10 10M13 3 3 13" />),
  More: make(
    <>
      <circle cx="3" cy="8" r="1" />
      <circle cx="8" cy="8" r="1" />
      <circle cx="13" cy="8" r="1" />
    </>
  ),
  Settings: make(
    <>
      <circle cx="8" cy="8" r="2" />
      <path d="M8 1.5v2M8 12.5v2M14.5 8h-2M3.5 8h-2m11-4.5L12 5m-8 6-1.5 1.5m10 0L12 11m-8-6L2.5 3.5" />
    </>
  ),
  Refresh: make(
    <>
      <path d="M2 8a6 6 0 0 1 11-3.5M14 8a6 6 0 0 1-11 3.5" />
      <path d="M11 1v3h3M5 15v-3H2" />
    </>
  ),
  Pin: make(
    <>
      <path d="M8 14v-3M5 3l1 6h4l1-6z" />
      <path d="M4 3h8" />
    </>
  ),
  Map: make(
    <>
      <path d="M2 4l4-2 4 2 4-2v10l-4 2-4-2-4 2z" />
      <path d="M6 2v10M10 4v10" />
    </>
  ),
  Clock: make(
    <>
      <circle cx="8" cy="8" r="6" />
      <path d="M8 4.5V8l2.5 1.5" />
    </>
  ),
  Warn: make(
    <>
      <path d="M8 2 14 13H2z" />
      <path d="M8 6.5v3M8 11.5v.01" />
    </>
  ),
  Receive: make(<path d="M2 13h12M8 2v8m-3-3 3 3 3-3" />),
  Tag: make(
    <>
      <path d="M2 8 8 2h5v5l-6 6z" />
      <circle cx="10.5" cy="5.5" r="0.6" />
    </>
  ),
  Card: make(
    <>
      <rect x="1.5" y="3.5" width="13" height="9" rx="1.5" />
      <path d="M1.5 6.5h13M3.5 10h3" />
    </>
  ),
  Users: make(
    <>
      <circle cx="6" cy="6" r="2.5" />
      <path d="M2 13a4 4 0 0 1 8 0" />
      <circle cx="11.5" cy="6" r="2" />
      <path d="M10 13a3.5 3.5 0 0 1 4-3.5" />
    </>
  ),
  User: make(
    <>
      <circle cx="8" cy="6" r="2.5" />
      <path d="M3 13.5a5 5 0 0 1 10 0" />
    </>
  ),
  DocCheck: make(
    <>
      <path d="M3 2h7l3 3v9H3z" />
      <path d="M10 2v3h3" />
      <path d="m5.5 9.5 1.5 1.5 3-3" />
    </>
  ),
  Building: make(
    <>
      <rect x="2.5" y="2.5" width="11" height="11" rx="0.5" />
      <path d="M5 5h1.5M5 7.5h1.5M5 10h1.5M9.5 5H11M9.5 7.5H11M9.5 10H11" />
    </>
  ),
  Factory: make(
    <>
      <path d="M2 13.5V7l3 1.5V7l3 1.5V7l3 1.5V4.5h2v9z" />
      <path d="M2 13.5h12" />
    </>
  ),
  Coin: make(
    <>
      <circle cx="8" cy="8" r="6" />
      <path d="M8 4.5v7M6 6h3a1.5 1.5 0 0 1 0 3H6h3a1.5 1.5 0 0 1 0 3H6" />
    </>
  ),
  Chart: make(
    <>
      <path d="M2 13.5h12M4 11V7M7.5 11V4M11 11V8.5" />
    </>
  ),
  Sun: make(
    <>
      <circle cx="8" cy="8" r="3" />
      <path d="M8 1v2M8 13v2M1 8h2M13 8h2M3 3l1.4 1.4M11.6 11.6 13 13M3 13l1.4-1.4M11.6 4.4 13 3" />
    </>
  ),
  Moon: make(<path d="M13 9.5A6 6 0 1 1 6.5 3a4.5 4.5 0 0 0 6.5 6.5z" />),
  System: make(
    <>
      <rect x="2" y="3" width="12" height="8" rx="1" />
      <path d="M5 13h6M8 11v2" />
    </>
  ),
  Globe: make(
    <>
      <circle cx="8" cy="8" r="6" />
      <path d="M2 8h12M8 2c1.5 2 2.5 4 2.5 6S9.5 14 8 14M8 2c-1.5 2-2.5 4-2.5 6s1 4 2.5 6" />
    </>
  ),
} as const;

export type IconKey = keyof typeof Icon;
