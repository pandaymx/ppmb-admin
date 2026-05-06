export interface MetaVo {
  title?: string;
  icon?: string;
  noCache?: boolean;
  link?: string;
}

export interface RouterVo {
  name: string;
  path: string;
  hidden?: boolean;
  component?: string;
  meta?: MetaVo;
  children?: RouterVo[];
}
