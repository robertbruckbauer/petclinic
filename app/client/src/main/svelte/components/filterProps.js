export default function filterProps(reservedProps, props) {
  return Object.keys(props).reduce(
    (acc, cur) =>
      cur.includes("$$") || reservedProps.includes(cur)
        ? acc
        : { ...acc, [cur]: props[cur] },
    {}
  );
}
