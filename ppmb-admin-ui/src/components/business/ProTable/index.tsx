import { useState } from 'react';
import { ProTable as AntdProTable, ProTableProps as AntdProTableProps } from '@ant-design/pro-components';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';

export interface ProTableProps<T, U> extends Omit<AntdProTableProps<T, U>, 'request'> {
  queryKey?: string[];
  queryFn?: (params: U & {
    pageSize?: number;
    current?: number;
    keyword?: string;
  }, sort: Record<string, any>, filter: Record<string, any>) => Promise<{
    data: T[];
    success: boolean;
    total: number;
  }>;
}

export function ProTable<T extends Record<string, any>, U extends Record<string, any> = Record<string, any>>(props: ProTableProps<T, U>) {
  const { t } = useTranslation();
  const { queryKey, queryFn, ...restProps } = props;

  const [queryParams, setQueryParams] = useState<U & {
    pageSize?: number;
    current?: number;
    keyword?: string;
  }>({} as any);

  const [sortParams, setSortParams] = useState<Record<string, any>>({});
  const [filterParams, setFilterParams] = useState<Record<string, any>>({});

  const { isLoading } = useQuery({
    queryKey: queryKey ? [...queryKey, queryParams, sortParams, filterParams] : ['pro-table-query', queryParams, sortParams, filterParams],
    queryFn: async () => {
      if (queryFn) {
        return queryFn(queryParams, sortParams, filterParams);
      }
      return { data: [], success: true, total: 0 };
    },
    enabled: !!queryFn, // Only run if queryFn is provided
  });

  const request = async (
    params: U & {
      pageSize?: number;
      current?: number;
      keyword?: string;
    },
    sort: Record<string, any>,
    filter: Record<string, any>
  ) => {
    // Update local state to trigger useQuery refetch
    setQueryParams(params);
    setSortParams(sort);
    setFilterParams(filter);

    // Ant Design ProTable expects data to be returned from request to update its internal state
    // We fetch immediately here to satisfy Antd, while React Query handles the background cache
    if (queryFn) {
        return queryFn(params, sort, filter);
    }
    return { data: [], success: true, total: 0 };
  };

  return (
    <AntdProTable<T, U>
      request={request}
      loading={isLoading}
      pagination={{
        defaultPageSize: 10,
        showSizeChanger: true,
      }}
      search={{
        labelWidth: 'auto',
        searchText: t('common.submit'),
        resetText: t('common.reset'),
      }}
      {...restProps}
    />
  );
}
