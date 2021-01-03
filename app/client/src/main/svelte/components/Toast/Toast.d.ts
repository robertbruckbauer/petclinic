import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';

export default class Toast extends SvelteTypedComponent<ToastProps, ToastEvents, ToastSlots> {
}

declare const _ToastProps: {
} & SvelteAllProps;

declare const _ToastEvents: {
};

declare const _ToastSlots: {
    default: {};
};

export declare type ToastProps = typeof _ToastProps;
export declare type ToastEvents = typeof _ToastEvents;
export declare type ToastSlots = typeof _ToastSlots;
export {
};
