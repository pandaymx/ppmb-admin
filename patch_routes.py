import re

with open('ppmb-admin-ui/src/router/routes.tsx', 'r') as f:
    content = f.read()

# Add import for Chat page
import_stmt = 'import ChatPage from "../pages/ai/Chat";\nimport { RobotOutlined } from "@ant-design/icons";\n'
content = content.replace('import DashboardPage from "../pages/dashboard/index";', 'import DashboardPage from "../pages/dashboard/index";\n' + import_stmt)

# Add route for AI Assistant
ai_route = """
      {
        path: "ai",
        element: <ChatPage />,
        meta: {
          title: "AI Assistant",
          icon: <RobotOutlined />,
        },
      },"""

# Insert ai route after dashboard route
content = re.sub(r'(index:\s*true,\s*element:\s*<DashboardPage\s*/>,\s*meta:\s*{\s*title:\s*"Dashboard",\s*icon:\s*<PieChartOutlined\s*/>,\s*},\s*},)', r'\1' + ai_route, content)

with open('ppmb-admin-ui/src/router/routes.tsx', 'w') as f:
    f.write(content)
