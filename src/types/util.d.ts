type UnionToIntersection<U> =
  (U extends any ? (x: U) => void : never) extends
    (x: infer I) => void ? I : never;

type EmitHandler<T extends Record<string, unknown[]>> =
  UnionToIntersection<
    {
      [K in keyof T]: (evt: K, ...args: T[K]) => void;
    }[keyof T]
  >;