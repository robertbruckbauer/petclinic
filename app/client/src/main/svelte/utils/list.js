export function mapify(l, key) {
  console.log(["mapify", l]);
  let m = new Map();
  l.forEach((e) => {
    let k = key(e);
    let v = m.get(k);
    if (v) {
      m.set(k, [...v, e]);
    } else {
      m.set(k, [e]);
    }
  });
  console.log(["mapify", m]);
  return m;
}
