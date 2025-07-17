/**
 * Formats the given number object in the
 * range 0 to 99 with a leading zero.
 *
 * @param {Number} n
 * @returns number string
 */
export function appendLeadingZero(n) {
  return n <= 9 ? "0" + n : "" + n;
}

/**
 * Returns the current date optionally
 * incremented by a number of days.
 *
 * @param {Number} delta
 * @returns {Date}
 */
export function today(delta = 0) {
  let date = new Date();
  if (delta) {
    date.setDate(date.getDate() + delta);
  }
  return date;
}

/**
 * Formats the given date objects.
 * Returns a string with format "YYYY-mm-dd".
 *
 * @param {Date} date
 * @returns date string
 */
export function formatDate(date) {
  return (
    date.getFullYear() +
    "-" +
    appendLeadingZero(date.getMonth() + 1) +
    "-" +
    appendLeadingZero(date.getDate())
  );
}

/**
 * Formats the given date objects.
 * Returns a string with format "HH:mm".
 *
 * @param {Date} date
 * @returns time string
 */
export function formatTime(date) {
  return (
    appendLeadingZero(date.getHours()) +
    ":" +
    appendLeadingZero(date.getMinutes())
  );
}

/**
 * Formats the given date objects.
 * Returns the short form.
 *
 * @param {Date} date
 * @returns weekday string
 */
export function formatWeekday(date) {
  switch (date?.getDay()) {
    case 0:
      return "SO";
    case 1:
      return "MO";
    case 2:
      return "DI";
    case 3:
      return "MI";
    case 4:
      return "DO";
    case 5:
      return "FR";
    case 6:
      return "SA";
  }
  throw new Error("unknown weekday");
}

/**
 * Collects all years in given range around the
 * current year in an item structure suitable
 * for use in selection lists.
 *
 * @param {number} prev
 * @param {number} next
 * @returns array of items
 */
export function collectAllYearItem(prev, next) {
  let now = new Date();
  let allYear = [];
  for (let i = prev; i > 0; i--) {
    allYear.push(now.getFullYear() - i);
  }
  allYear.push(now.getFullYear());
  for (let i = 1; i < next; i++) {
    allYear.push(now.getFullYear() + i);
  }
  return allYear.map((e) => ({ text: e.toString(), value: e }));
}

/**
 * Collects a months of a year in an item structure
 * suitable for use in selection lists.
 *
 * @returns array of items
 */
export function collectAllMonthItem() {
  return [
    { text: "Januar", code: "JAN", value: 1 },
    { text: "Februar", code: "FEB", value: 2 },
    { text: "März", code: "MAR", value: 3 },
    { text: "April", code: "APR", value: 4 },
    { text: "Mai", code: "MAI", value: 5 },
    { text: "Juni", code: "JUN", value: 6 },
    { text: "Juli", code: "JUL", value: 7 },
    { text: "August", code: "AUG", value: 8 },
    { text: "September", code: "SEP", value: 9 },
    { text: "Oktober", code: "OKT", value: 10 },
    { text: "November", code: "NOV", value: 11 },
    { text: "Dezember", code: "DEZ", value: 12 },
  ];
}

/**
 * Collects all dates of a given month in the given year.
 * Format is "YYYY-mm-dd".
 *
 * @param {number} year
 * @param {number} month
 * @returns array of date strings
 */
export function collectAllDateOfMonth(year, month) {
  let allDateOfMoth = [];
  let date = new Date(year, month - 1, 1);
  while (date.getMonth() === month - 1) {
    allDateOfMoth.push(formatDate(date));
    date.setDate(date.getDate() + 1);
  }
  return allDateOfMoth;
}

/**
 * Computes a difference in minutes for given times.
 *
 * @param {String} von with format HH:mm
 * @param {String} bis with format HH:mm
 * @returns difference in minutes
 */
export function computeDauer(von, bis) {
  if (!von || !bis) return undefined;
  let vonMs = new Date("2000-01-01 " + von).getTime();
  let bisMs = new Date("2000-01-01 " + bis).getTime();
  return Math.abs(Math.round((bisMs - vonMs) / 60000));
}

/**
 * Computes a new time with a given difference in minutes.
 *
 * @param {String} von with format HH:mm
 * @param {number} dauer in minutes
 * @returns time with format HH:mm
 */
export function computeBis(von, dauer) {
  if (!von || isNaN(dauer)) return undefined;
  let vonMs = new Date("2000-01-01 " + von).getTime();
  let dauerMs = dauer * 60000;
  return formatTime(new Date(vonMs + dauerMs));
}
