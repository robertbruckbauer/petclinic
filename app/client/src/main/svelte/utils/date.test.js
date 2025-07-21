import { describe, it, expect, vi, beforeEach } from "vitest";
import { today } from "./date";
import { formatWeekday } from "./date";
import { formatDate } from "./date";
import { formatTime } from "./date";
import { collectAllYearItem } from "./date";
import { collectAllMonthItem } from "./date";
import { collectAllDateOfMonth } from "./date";
import { computeDauer } from "./date";
import { computeBis } from "./date";

describe("date", () => {
  beforeEach(() => {});

  describe("today", () => {
    it.each([
      [0, new Date(2024, 0, 15)],
      [1, new Date(2024, 0, 16)],
      [-1, new Date(2024, 0, 14)],
      [45, new Date(2024, 1, 29)], // Leap year
    ])("should return date for %i", (delta, date) => {
      try {
        vi.useFakeTimers();
        vi.setSystemTime(new Date(2024, 0, 15));
        expect(today(delta)).toEqual(date);
      } finally {
        vi.useRealTimers();
      }
    });
  });

  describe("formatWeekday", () => {
    it("should handle unknown weekday", () => {
      expect(() => formatWeekday(null)).toThrow("unknown weekday");
      expect(() => formatWeekday(undefined)).toThrow("unknown weekday");
    });

    it.each([
      [new Date("2023-01-01"), "SO"],
      [new Date("2023-01-02"), "MO"],
      [new Date("2023-01-03"), "DI"],
      [new Date("2023-01-04"), "MI"],
      [new Date("2023-01-05"), "DO"],
      [new Date("2023-01-06"), "FR"],
      [new Date("2023-01-07"), "SA"],
    ])("should return correct weekday for %s", (date, weekday) => {
      expect(formatWeekday(date)).toBe(weekday);
    });
  });

  describe("formatTime", () => {
    it.each([
      [new Date(2023, 0, 1, 9, 5), "09:05"], // Single digit hour and minute
      [new Date(2023, 0, 1, 0, 0), "00:00"], // Midnight
      [new Date(2023, 0, 1, 23, 59), "23:59"], // Just before midnight
      [new Date(2023, 0, 1, 12, 30), "12:30"], // Noon
      [new Date(2023, 0, 1, 15, 15), "15:15"], // 24-hour format
    ])("should format time %s", (date, timeString) => {
      expect(formatTime(date)).toBe(timeString);
    });
  });

  describe("formatDate", () => {
    it.each([
      [new Date(2023, 0, 1), "2023-01-01"], // January
      [new Date(2023, 11, 31), "2023-12-31"], // December
      [new Date(2023, 8, 9), "2023-09-09"], // Single digit month/day
      [new Date(2024, 1, 29), "2024-02-29"], // Leap year
      [new Date(2025, 11, 31), "2025-12-31"], // Year boundary
    ])("should format date %s", (date, dateString) => {
      expect(formatDate(date)).toBe(dateString);
    });
  });

  describe("collectAllYearItem", () => {
    it.each([
      [0, 1],
      [2, 4],
    ])("should collect all years for %i", (value, count) => {
      const allItem = collectAllYearItem(value, value);
      expect(allItem.length).toBe(count);
    });
  });

  describe("collectAllMonthItem", () => {
    it("should collect all months", () => {
      const allItem = collectAllMonthItem();
      expect(allItem.length).toBe(12);
      expect(allItem[0].value).toBe(1);
      expect(Math.min(...allItem.map((e) => e.value))).toEqual(1);
      expect(Math.max(...allItem.map((e) => e.value))).toEqual(12);
    });
  });

  describe("collectAllDateOfMonth", () => {
    it.each([
      [2023, 1, 31],
      [2023, 2, 28],
      [2024, 2, 29],
      [2023, 12, 31],
    ])("should collect all dates for month %i-%i", (year, month, count) => {
      const allItem = collectAllDateOfMonth(year, month);
      expect(allItem.length).toBe(count);
      const allDate = allItem.map((e) => new Date(e));
      expect(allDate[0].getDate()).toBe(1);
      expect(allDate[count - 1].getDate()).toBe(count);
      // same year for all items
      expect(Math.min(...allDate.map((e) => e.getFullYear()))).toEqual(year);
      expect(Math.max(...allDate.map((e) => e.getFullYear()))).toEqual(year);
      // same month for all items
      expect(Math.min(...allDate.map((e) => e.getMonth()))).toEqual(month - 1);
      expect(Math.max(...allDate.map((e) => e.getMonth()))).toEqual(month - 1);
    });
  });

  describe("computeDauer", () => {
    it.each([
      ["00:00", "23:59", 1439],
      ["23:59", "00:00", 1439],
      ["00:00", "24:00", 1440],
      ["24:00", "00:00", 1440],
      ["00:00", "00:01", 1],
      ["00:01", "00:00", 1],
      ["23:59", "24:00", 1],
      ["24:00", "23:59", 1],
    ])("should compute a duration from %s to %s", (von, bis, dauer) => {
      expect(computeDauer(von, bis)).toBe(dauer);
    });

    it("should not compute a duration", () => {
      expect(computeDauer("00:00", null)).toBe(undefined);
      expect(computeDauer("00:00", undefined)).toBe(undefined);
      expect(computeDauer("00:00", "")).toBe(undefined);
      expect(computeDauer(null, "00:00")).toBe(undefined);
      expect(computeDauer(undefined, "00:00")).toBe(undefined);
      expect(computeDauer("", "00:00")).toBe(undefined);
    });
  });

  describe("computeBis", () => {
    it.each([
      ["00:00", 1439, "23:59"],
      ["00:00", 1440, "00:00"],
      ["00:00", 1, "00:01"],
      ["23:59", 1, "00:00"],
      ["24:00", 1, "00:01"],
    ])("should compute a time from %s to %i", (von, dauer, bis) => {
      expect(computeBis(von, dauer)).toBe(bis);
    });

    it("should not compute a time", () => {
      expect(computeBis("00:00", NaN)).toBe(undefined);
      expect(computeBis("00:00", undefined)).toBe(undefined);
      expect(computeBis(null, 1)).toBe(undefined);
      expect(computeBis(undefined, 1)).toBe(undefined);
      expect(computeBis("", 1)).toBe(undefined);
    });
  });
});
