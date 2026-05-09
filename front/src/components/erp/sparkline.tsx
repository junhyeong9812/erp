interface Props {
  points: number[];
  width?: number;
  height?: number;
  className?: string;
}

export function Sparkline({
  points,
  width = 76,
  height = 28,
  className,
}: Props) {
  if (!points.length) return null;

  const min = Math.min(...points);
  const max = Math.max(...points);
  const range = max - min || 1;
  const norm = (v: number) =>
    height - 2 - ((v - min) / range) * (height - 4);
  const step = points.length > 1 ? width / (points.length - 1) : 0;

  const d = points
    .map((p, i) => `${i === 0 ? "M" : "L"} ${i * step} ${norm(p)}`)
    .join(" ");

  const lastIdx = points.length - 1;
  const lastValue = points[lastIdx]!;

  return (
    <svg
      className={className}
      viewBox={`0 0 ${width} ${height}`}
      width={width}
      height={height}
      aria-hidden
    >
      <path
        d={d}
        fill="none"
        stroke="var(--color-accent)"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        opacity="0.85"
      />
      <circle
        cx={lastIdx * step}
        cy={norm(lastValue)}
        r="2"
        fill="var(--color-accent)"
      />
    </svg>
  );
}
