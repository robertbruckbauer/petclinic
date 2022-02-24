/**
 * Formats the given number object.
 * Returns a string with leading zero.
 *
 * @param {Number} n
 * @returns number string
 */
export function appendLeadingZeroes(n) {
  if (n <= 9) {
    return "0" + n;
  }
  return n;
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
    appendLeadingZeroes(date.getMonth() + 1) +
    "-" +
    appendLeadingZeroes(date.getDate())
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
    appendLeadingZeroes(date.getHours()) +
    ":" +
    appendLeadingZeroes(date.getMinutes())
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
  switch (date.getDay()) {
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
  throw new Error("unkown weekday");
}
