export interface ServiceStatus {
    name: string;
    url: string;
    status: 'UP' | 'DOWN' | 'CHECKING';
}
