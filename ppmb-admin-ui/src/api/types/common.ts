export interface Result<T = any> {
  code: string;
  message: string;
  data: T;
}

export interface PageQuery {
  page: number;
  size: number;
  [key: string]: any;
}

export interface PageResult<T> {
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  content: T[];
}
