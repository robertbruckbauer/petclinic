export function mapify(allItem, keyFunction, sortFunction) {
  if (!allItem) {
    throw new Error("items are required");
  }
  if (!keyFunction) {
    throw new Error("key function is required");
  }
  if (!sortFunction) {
    throw new Error("sort function is required");
  }
  let allItemMapped = new Map();
  allItem.forEach((e) => {
    let k = keyFunction(e);
    let v = allItemMapped.get(k);
    if (v) {
      allItemMapped.set(k, [...v, e].sort(sortFunction));
    } else {
      allItemMapped.set(k, [e]);
    }
  });
  return allItemMapped;
}
