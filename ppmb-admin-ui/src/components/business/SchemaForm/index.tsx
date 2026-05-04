import { BetaSchemaForm, ProFormProps } from "@ant-design/pro-components";
import type { ProFormColumnsType } from "@ant-design/pro-components";
import { useTranslation } from "react-i18next";

export interface SchemaFormProps<T> extends Omit<ProFormProps<T>, "action"> {
  columns: ProFormColumnsType<T>[];
  layoutType?:
    | "Form"
    | "ModalForm"
    | "DrawerForm"
    | "QueryFilter"
    | "LightFilter"
    | "StepForm";
  action?: any; // To avoid type conflicts
}

export function SchemaForm<T extends Record<string, any>>(
  props: SchemaFormProps<T>,
) {
  const { t } = useTranslation();
  const { columns, layoutType = "Form", ...restProps } = props;

  return (
    <BetaSchemaForm<T>
      layoutType={layoutType as any}
      columns={columns as any}
      submitter={{
        searchConfig: {
          submitText: t("common.submit"),
          resetText: t("common.reset"),
        },
      }}
      {...(restProps as any)}
    />
  );
}
