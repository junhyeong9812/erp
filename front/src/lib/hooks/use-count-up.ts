"use client";

import { useEffect, useRef, useState } from "react";

export function useCountUp(target: number, duration = 700): number {
  const [val, setVal] = useState(target);
  const fromRef = useRef(target);
  const targetRef = useRef(target);

  useEffect(() => {
    if (target === targetRef.current) return;
    fromRef.current = val;
    targetRef.current = target;

    const start = performance.now();
    let raf = 0;

    const tick = (t: number) => {
      const p = Math.min(1, (t - start) / duration);
      const eased = 1 - Math.pow(1 - p, 3);
      const next = fromRef.current + (target - fromRef.current) * eased;
      setVal(next);
      if (p < 1) {
        raf = requestAnimationFrame(tick);
      } else {
        setVal(target);
      }
    };

    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [target, duration, val]);

  return val;
}
