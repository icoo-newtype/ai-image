interface SortState {
  use: boolean;
  type: 'asc' | 'desc';
  field: string | ((row: T) => any) | null;
}

type Variant = 'primary' | 'ghost' | 'outline-secondary' | 'secondary' | 'outline-dark' | 'outline' | 'success' | 'default' | 'light' | 'dark' | 'danger';